# 概要
Sample project for background location acquisition on Android 10 and above<br>

It is better to get location information with the foreground service<br>

If the app wants to get location information in the background from Android 10, it needs permission of [ACCESS_BACKGROUND_LOCATION](https://developer.android.com/reference/android/Manifest.permission#ACCESS_BACKGROUND_LOCATION).

However, when handling location information in the foreground service, it is possible to acquire location information without requiring permission for ACCESS_BACKGROUND_LOCATION.
In that case, you need to specify `location` in [foregroundServiceType] (https://developer.android.com/reference/android/R.attr#foregroundServiceType) of the foreground service.

[link1](https://developer.android.com/about/versions/10/features#fg-service-types)
[link2](https://developer.android.com/about/versions/10/highlights#privacy_for_users)
[link3](https://developer.android.com/about/versions/10/privacy/changes#app-access-device-location)
[link4](https://developer.android.com/about/versions/13/behavior-changes-all#fgs-manager)
[link5](https://developer.android.com/about/versions/13/changes/notification-permission)
[link6](https://developer.android.com/reference/android/app/NotificationManager#areNotificationsEnabled())
[link7](https://moneyforward.com/engineers_blog/2022/04/11/android13-notification-runtime-permission/)
[link8](https://developer.android.com/training/location/permissions)


# Check if you can get location information in the foreground / background of the app

| device | foreground | background |
|:---|:---:|:---|
|Pixel 4 OS:13 | OK | OK |
|Pixel 4 OS:12 | OK | OK |
|Pixel 4 OS:10 | OK | OK |

## capture Pixel 4 OS:10

| Screen | Notification |
|:---|:---:|
|<img src="https://github.com/LeoAndo/LocationUpdateBackgroundSample/blob/main/capture_API29.png" width=320 /> |<img src="https://github.com/LeoAndo/LocationUpdateBackgroundSample/blob/main/capture_notification_API29.png" width=320 /> |


## capture Pixel 4 OS:12

| Screen | Notification |
|:---|:---:|
|<img src="https://github.com/LeoAndo/LocationUpdateBackgroundSample/blob/main/capture_API32.png" width=320 /> |<img src="https://github.com/LeoAndo/LocationUpdateBackgroundSample/blob/main/capture_notification_API32.png" width=320 /> |

## capture Pixel 4 OS:13

| Screen | Notification |
|:---|:---:|
|<img src="https://github.com/LeoAndo/LocationUpdateBackgroundSample/blob/main/capture_API33.png" width=320 /> |<img src="https://github.com/LeoAndo/LocationUpdateBackgroundSample/blob/main/capture_notification_API33.png" width=320 /> |

# implements memo

[foreground service](https://github.com/LeoAndo/LocationUpdateBackgroundSample/tree/main/app/src/useForegroundService)

background
- [Use `PendingIntent & BroadcastReceiver`](https://github.com/LeoAndo/LocationUpdateBackgroundSample/tree/main/app/src/usePendingIntent)
- [Use `LocationCallback`](https://github.com/LeoAndo/LocationUpdateBackgroundSample/tree/main/app/src/useLocationCallback)

# From Android 10, the item "Allow only while using the app" has been added to the selection items on the app settings screen.

If **"Allow only while using the app"** is checked, location information in the background will not be performed.

<img src="https://user-images.githubusercontent.com/16476224/115006050-625da080-9ee3-11eb-8849-d72701fcdff9.png" width=320 />
