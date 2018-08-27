package org.hyperskill.api.objects.users

import org.stepik.api.objects.AbstractObject

class HSUser : AbstractObject() {
    var isGuest = false
    
    var fullname: String? = ""
}
