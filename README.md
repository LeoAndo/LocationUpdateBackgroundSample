# LocationUpdateBackgroundSample
sample for Get location in the background(Android 10 or later)

Android 10からアプリがバックグランド中に位置情報を取得したい場合は、[ACCESS_BACKGROUND_LOCATION](https://developer.android.com/reference/android/Manifest.permission#ACCESS_BACKGROUND_LOCATION) の許可が必要になった。

フォアグランドサービスが位置情報を扱う場合は、[foregroundServiceType](https://developer.android.com/reference/android/R.attr#foregroundServiceType)に`location`を指定する必要がある。

[link1](https://developer.android.com/about/versions/10/features#fg-service-types)
[link2](https://developer.android.com/about/versions/10/highlights#privacy_for_users)
[link3](https://developer.android.com/about/versions/10/privacy/changes#app-access-device-location)

# アプリのforeground/backgroundで位置情報を取得できるか確認
| device | foreground | background |
|:---|:---:|:---|
|Pixel 5 OS:11 | OK | OK |
