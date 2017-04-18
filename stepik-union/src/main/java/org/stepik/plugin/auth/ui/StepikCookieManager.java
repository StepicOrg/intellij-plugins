package org.stepik.plugin.auth.ui;

import java.net.CookieManager;

class StepikCookieManager extends CookieManager {
    StepikCookieManager() {
        super(new StepikCookieStore(), null);
    }
}
