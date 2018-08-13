package org.stepik.core.courseFormat

import com.intellij.openapi.project.Project
import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamOmitField
import org.stepik.api.client.StepikApiClient
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.StudyObject
import org.stepik.api.objects.progresses.Progresses
import org.stepik.core.auth.StepikAuthManager
import org.stepik.core.auth.StepikAuthManager.isAuthenticated
import org.stepik.core.courseFormat.StudyStatus.FAILED
import org.stepik.core.courseFormat.StudyStatus.SOLVED
import org.stepik.core.courseFormat.StudyStatus.UNCHECKED
import org.stepik.core.utils.batch
import org.stepik.core.utils.refreshProjectView
import java.util.concurrent.Executors

abstract class Node(project: Project? = null,
                    stepikApiClient: StepikApiClient? = null,
                    data: StudyObject? = null) : StudyNode {
    
    override var parent: StudyNode? = null
    
    @XStreamAlias("data")
    private var _data: StudyObject? = null
    
    final override var data: StudyObject
        get() {
            if (_data == null) {
                try {
                    _data = dataClass.newInstance()
                } catch (e: Exception) {
                    logger.warn("Can't create data instance: ${dataClass.name}")
                }
            }
            return _data ?: throw AssertionError("Set to null by another thread")
        }
        set(value) {
            _data = value
        }
    
    @XStreamOmitField
    override var project: Project? = null
    
    init {
        this._data = data
        init(project, stepikApiClient, null)
    }
    
    @XStreamAlias("children")
    private var _children: List<Node>? = null
    
    override val children: List<Node>
        get() {
            if (_children == null) {
                return emptyList()
            }
            return _children ?: throw AssertionError("Set to null by another thread")
        }
    
    fun setChildren(children: List<Node>) {
        _children = children
    }
    
    override var wasDeleted: Boolean = false
    
    @XStreamOmitField
    @Volatile
    private var checkingStatus = false
    
    @XStreamOmitField
    @Volatile
    private var _status: StudyStatus? = UNCHECKED
    
    private fun updateStatus() {
        val stepikApiClient = StepikAuthManager.authAndGetStepikApiClient()
        if (!isAuthenticated) {
            return
        }
        
        val progressIdToNode = children.filter { it._status == UNCHECKED && !it.checkingStatus }
                .associateBy { it.data.progress }
        
        if (progressIdToNode.isNotEmpty()) {
            progressIdToNode.keys.batch(100)
                    .forEach { ids ->
                        var page = 1
                        var progresses: Progresses
                        do {
                            val query = stepikApiClient.progresses()
                                    .get()
                                    .id(*ids.toTypedArray())
                                    .page(page++)
                            try {
                                progresses = query.execute()
                                progresses.forEach { progress ->
                                    val id = progress.id
                                    val node = progressIdToNode[id] ?: if (id == data.progress) this else null
                                    node?.apply {
                                        _status = if (progress.isPassed) SOLVED else FAILED
                                        checkingStatus = false
                                    }
                                }
                            } catch (e: StepikClientException) {
                                logger.warn(e)
                                break
                            }
                        } while (progresses.meta.hasNext)
                    }
        }
        
        refreshProjectView(project)
    }
    
    override var status: StudyStatus
        get() {
            val status = _status
            if ((status == UNCHECKED || status == null) && !checkingStatus) {
                checkingStatus = true
                
                executor.execute {
                    updateStatus()
                }
            }
            
            return status ?: UNCHECKED
        }
        set(value) {
            if (_status !== value) {
                if (value === FAILED && _status === SOLVED) {
                    return
                }
                _status = value
                var parent = parent
                while (parent != null) {
                    parent.status = UNCHECKED
                    parent = parent.parent
                }
            }
        }
    
    override val isLeaf: Boolean
        get() = children.isEmpty()
    
    override val path: String
        get() {
            val parentPath = parent?.path ?: return ""
            
            if (parentPath.isEmpty()) {
                return directory
            }
            
            return "$parentPath/$directory"
        }
    
    override val directory: String
        get() {
            parent ?: return ""
            return "$directoryPrefix$id"
        }
    
    open val directoryPrefix
        get() = ""
    
    private val mapNodes: Map<Long, StudyNode>
        get() = children.associateBy { it.id }
    
    protected abstract val childClass: Class<out Node>
    
    protected abstract val childDataClass: Class<out StudyObject>
    
    abstract val dataClass: Class<out StudyObject>
    
    override var id: Long
        get() = data.id
        set(id) {
            data.id = id
        }
    
    override val position: Int
        get() = data.position
    
    override val name: String
        get() = data.title
    
    override fun getPrevChild(current: StudyNode?): StudyNode? {
        return children.lastOrNull {
            !it.wasDeleted && (current == null || it.position < current.position)
        }
    }
    
    override fun getNextChild(current: StudyNode?): StudyNode? {
        return children.firstOrNull {
            !it.wasDeleted && (current == null || it.position > current.position)
        }
    }
    
    override fun getChildById(id: Long) = mapNodes[id]
    
    override fun getChildByClassAndId(clazz: Class<out StudyNode>, id: Long): StudyNode? {
        return if (childDataClass == clazz) {
            getChildById(id)
        } else {
            children.mapNotNull { it.getChildByClassAndId(clazz, id) }
                    .firstOrNull()
        }
    }
    
    override fun getChildByPosition(position: Int): StudyNode? {
        return children.first { it.position == position }
    }
    
    protected open fun getChildDataList(stepikApiClient: StepikApiClient): List<StudyObject> {
        return emptyList()
    }
    
    final override fun init(project: Project?, stepikApiClient: StepikApiClient?, parent: StudyNode?) {
        beforeInit()
        
        this.parent = parent
        this.project = project
        
        if (stepikApiClient != null) {
            val processed = mutableListOf<StudyNode>()
            val children = this.children.toMutableList()
            
            for (data in getChildDataList(stepikApiClient)) {
                var child = mapNodes[data.id]
                if (child == null) {
                    try {
                        child = childClass.newInstance()
                        children.add(child)
                    } catch (e: Exception) {
                        logger.warn("Can't get new instance for child", e)
                        break
                    }
                }
                child!!.data = data
                child.wasDeleted = wasDeleted
                processed.add(child)
            }
            _children = children.sortedWith(StudyNodeComparator)
            
            children.filterNot { it in processed }
                    .forEach { it.wasDeleted = true }
        }
        
        children.forEach { it.init(project, stepikApiClient, this) }
    }
    
    open fun beforeInit() {
    
    }
    
    override fun reloadData(project: Project, stepikApiClient: StepikApiClient) {
        if (loadData(stepikApiClient, data.id)) {
            init(project, stepikApiClient)
        }
    }
    
    protected abstract fun loadData(stepikApiClient: StepikApiClient, id: Long): Boolean
    
    override fun resetStatus() {
        children.forEach { it.resetStatus() }
        status = UNCHECKED
    }
    
    override fun passed() {
        status = SOLVED
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        val node = other as Node
        
        if (wasDeleted != node.wasDeleted) return false
        if (data != node.data) return false
        
        if (children != node.children) return false
        
        return status === node.status
    }
    
    override fun hashCode(): Int {
        var result = data.hashCode()
        result = 31 * result + children.hashCode()
        result = 31 * result + wasDeleted.hashCode()
        result = 31 * result + status.hashCode()
        return result
    }
    
    companion object {
        private val executor = Executors.newFixedThreadPool(5)
    }
}
