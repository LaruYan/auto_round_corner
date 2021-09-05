# auto_round_corner
Set corner radius automatically via writing Secure Settings, set this again when orientation changes (Samsung One UI)

## It does
- Set screen corner radius via setting secure settings.
- do this again when orientation changes.
- App is [DirectBoot aware](https://developer.android.com/training/articles/direct-boot). so even it works right after boot.

## requires
- Enable `USB Debugging` of your device and plug it to your desktop, then run a command `adb shell pm grant moe.laruyan.setroundcorner android.permission.WRITE_SECURE_SETTINGS`, to grant an app writiting secure settings permission. [Download ADB here](https://developer.android.com/studio/releases/platform-tools?hl=sl)
- You can safely disable `USB Debugging` and `Developer Options` after granting permission. 

## features missing : TODO
1. user-settable round radius.
2. "apply now" function. (requires step 1)
