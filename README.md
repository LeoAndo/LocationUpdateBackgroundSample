
# Overview
sample for Get location in the background(Android 10 or later)

[日本語版README](https://github.com/LeoAndo/LocationUpdateBackgroundSample/blob/main/readme/README_JP.md)

# First of All
If the app wants to get location information in the background from Android 10, it needs permission of [ACCESS_BACKGROUND_LOCATION](https://developer.android.com/reference/android/Manifest.permission#ACCESS_BACKGROUND_LOCATION).

If the foreground service handles location information, you need to specify `location` in [foregroundServiceType](https://developer.android.com/reference/android/R.attr#foregroundServiceType).

[link1](https://developer.android.com/about/versions/10/features#fg-service-types)
[link2](https://developer.android.com/about/versions/10/highlights#privacy_for_users)
[link3](https://developer.android.com/about/versions/10/privacy/changes#app-access-device-location)
[link4](https://developer.android.com/training/location/request-updates)

# Check if you can get location information in the foreground / background of the app

**During the background, the location information acquisition interval was longer than during the foreground.**

| device | foreground | background |
|:---|:---:|:---|
|Pixel 5 OS:11 | OK | OK |
|Pixel 4 OS:10 | OK | OK |

## capture Pixel 4 OS:10
<img src="https://github.com/LeoAndo/LocationUpdateBackgroundSample/blob/main/capture.png" width=320/>

# Implemantation(Memo)

To get location information when the app is in the background,
- Implement with a combination of `PendingIntent & BroadcastReceiver`.
https://github.com/LeoAndo/LocationUpdateBackgroundSample/tree/main/app/src/usePendingIntent/java/com/template/locationupdatebackgroundsample
or
- **Implement by using `LocationCallback`. `LocationCallback` is less implemented.**
https://github.com/LeoAndo/LocationUpdateBackgroundSample/tree/main/app/src/useLocationCallback/java/com/template/locationupdatebackgroundsample

# From Android 10, the item "Allow only while using the app" has been added to the selection items on the app settings screen.

If **"Allow only while using the app"** is checked, location information in the background will not be performed.

<img src="https://user-images.githubusercontent.com/16476224/115006050-625da080-9ee3-11eb-8849-d72701fcdff9.png" width=320 />
