# LocationUpdateBackgroundSample
sample for Get location in the background(Android 10 or later)

Android 10からアプリがバックグランド中に位置情報を取得したい場合は、[ACCESS_BACKGROUND_LOCATION](https://developer.android.com/reference/android/Manifest.permission#ACCESS_BACKGROUND_LOCATION) の許可が必要になった。

フォアグランドサービスが位置情報を扱う場合は、[foregroundServiceType](https://developer.android.com/reference/android/R.attr#foregroundServiceType)に`location`を指定する必要がある。

[link1](https://developer.android.com/about/versions/10/features#fg-service-types)
[link2](https://developer.android.com/about/versions/10/highlights#privacy_for_users)
[link3](https://developer.android.com/about/versions/10/privacy/changes#app-access-device-location)

# アプリのforeground/backgroundで位置情報を取得できるか確認

ただし、background中はforegroundの時よりも位置情報の取得間隔が長かった
| device | foreground | background |
|:---|:---:|:---|
|Pixel 5 OS:11 | OK | OK |
|Pixel 4 OS:10 | OK | OK |
|Pixel 4 OS:9 | OK | OK |
|Pixel 4 OS:8 | OK | OK |

## capture Pixel 4 OS:11
<img src="https://user-images.githubusercontent.com/16476224/121790680-a8ea2580-cc1c-11eb-9a09-60137d6c13d8.png" width=320/>

# 実装メモ
アプリがバックグランド時の位置情報取得には、`PendingIntent & BroadcastReceiver`の組み合わせで実装する。
もしくは、`LocationCallback`を使う方法で実装する。`LocationCallback`の方が実装量は少ない。

# アプリの設定画面の選択項目もAndroid 10から「アプリの使用中のみ許可する」項目が追加された。

「アプリの使用中のみ許可する」にチェックされていると、バックグラウンドでの位置情報を行わない

<img src="https://user-images.githubusercontent.com/16476224/115006050-625da080-9ee3-11eb-8849-d72701fcdff9.png" width=320 />
