# 概要
Android 10以降のバックグラウンドでの位置情報取得のサンプルプロジェクト<br>

結論から言うと、フォアグランドサービスで位置情報取得した方が良い。<br>

Android 10からアプリがバックグランド中に位置情報を取得したい場合は、[ACCESS_BACKGROUND_LOCATION](https://developer.android.com/reference/android/Manifest.permission#ACCESS_BACKGROUND_LOCATION) の許可が必要になった。

ただし、フォアグランドサービスで位置情報を扱う場合は、ACCESS_BACKGROUND_LOCATIONの許可を必要とせずに、位置情報取得が可能。
その場合、フォアグランドサービスの[foregroundServiceType](https://developer.android.com/reference/android/R.attr#foregroundServiceType)に`location`を指定する必要がある。

[link1](https://developer.android.com/about/versions/10/features#fg-service-types)
[link2](https://developer.android.com/about/versions/10/highlights#privacy_for_users)
[link3](https://developer.android.com/about/versions/10/privacy/changes#app-access-device-location)
[link4](https://developer.android.com/about/versions/13/behavior-changes-all#fgs-manager)
[link5](https://developer.android.com/about/versions/13/changes/notification-permission)
[link6](https://developer.android.com/reference/android/app/NotificationManager#areNotificationsEnabled())
[link7](https://moneyforward.com/engineers_blog/2022/04/11/android13-notification-runtime-permission/)
[link8](https://developer.android.com/training/location/permissions)


# アプリのforeground/backgroundで位置情報を取得できるか確認

| device | foreground | background |
|:---|:---:|:---|
|Pixel 4 OS:13 |  |  |
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



# 実装メモ

[foreground serviceの位置情報取得](https://github.com/LeoAndo/LocationUpdateBackgroundSample/tree/main/app/src/useForegroundService)

background時の位置情報取得
- [`PendingIntent & BroadcastReceiver`を使う方法](https://github.com/LeoAndo/LocationUpdateBackgroundSample/tree/main/app/src/usePendingIntent)
- [`LocationCallback`を使う方法](https://github.com/LeoAndo/LocationUpdateBackgroundSample/tree/main/app/src/useLocationCallback)

# アプリの設定画面の選択項目もAndroid 10から「アプリの使用中のみ許可する」項目が追加された。

「アプリの使用中のみ許可する」にチェックされていると、バックグラウンドでの位置情報を行わない

<img src="https://user-images.githubusercontent.com/16476224/115006050-625da080-9ee3-11eb-8849-d72701fcdff9.png" width=320 />
