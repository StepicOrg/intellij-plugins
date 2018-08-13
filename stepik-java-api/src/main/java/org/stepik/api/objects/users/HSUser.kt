package org.stepik.api.objects.users

class HSUser : User() {
    override fun isGuest(): Boolean {
        return false
    }
}
