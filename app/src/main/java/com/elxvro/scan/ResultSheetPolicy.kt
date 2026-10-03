package com.elxvro.scan

object ResultSheetPolicy {
    fun showPrimaryAction(type: SmartActionType): Boolean =
        type != SmartActionType.SHARE_TEXT
}
