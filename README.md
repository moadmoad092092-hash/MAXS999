# APPIFY — مشروع Android حقيقي

هذا المجلد هو مصدر Android كامل قابل للبناء. التطبيق مكتوب بـ **Kotlin** ويستخدم **Android WebView** لفتح موقع HTTPS، مع زر الرجوع، شريط تحميل، رسالة انقطاع الإنترنت، ومنع الاتصالات غير المشفرة.

## تخصيص التطبيق

1. من واجهة APPIFY في المجلد الرئيسي، أدخل اسم التطبيق والرابط واللون والحرف.
2. نزّل ملف `app-config.json`.
3. استبدل الملف الموجود هنا في `app/src/main/assets/app-config.json`.
4. أيقونة المشغّل الافتراضية خضراء وبها حرف A. لتغيير رسمها، عدّل `app/src/main/res/drawable/ic_launcher.xml` أو استبدله بأيقونة Android مناسبة.

ملف الإعدادات:

```json
{
  "appName": "موقعي",
  "siteUrl": "https://example.com",
  "themeColor": "#19C37D",
  "iconText": "م"
}
```

الرابط يجب أن يكون HTTPS. يمنع التطبيق `cleartextTraffic`، ولا يسمح WebView بالتنقل إلى روابط HTTP.

## البناء مجانًا من الهاتف عبر GitHub Actions

لا تحتاج إلى كمبيوتر:

1. أنشئ حسابًا مجانيًا على GitHub وأنشئ مستودعًا جديدًا (يفضّل أن يكون عامًا للاستفادة من الدقائق المجانية).
2. ارفع **محتويات مجلد `android-app`** إلى جذر المستودع، بما فيها مجلد `.github`.
3. ارفع ملف `app-config.json` المعدل إلى `app/src/main/assets/`.
4. افتح تبويب **Actions** ثم شغّل workflow باسم **Build APPIFY APK** عبر **Run workflow**.
5. بعد نجاح كل الخطوات، افتح صفحة التشغيل، ومن قسم **Artifacts** نزّل `app-debug-apk`.
6. فك الضغط على الهاتف، فعّل السماح بالتثبيت من هذا المصدر عند طلب Android، ثم افتح ملف APK وثبّته.

> لا يظهر APK ولا يُقال إنه جاهز إلا بعد أن تصبح حالة GitHub Actions خضراء ويظهر Artifact فعلًا. هذا المشروع لا يتصل بخدمة مدفوعة.

## البناء محليًا (اختياري)

يتطلب Android SDK وJDK 17:

```bash
./gradlew assembleDebug
```

الناتج سيكون في `app/build/outputs/apk/debug/app-debug.apk`.

لنسخة Release موقعة، استخدم مفتاح توقيعك وGradle signing config. النسخة Debug الناتجة من GitHub مناسبة للتجربة وليست للنشر في Google Play.

## حدود الخدمات المجانية

- GitHub Actions مجاني للمستودعات العامة ضمن سياسات GitHub الحالية؛ للمستودعات الخاصة يوجد حد دقائق مجاني شهري يحدده حساب GitHub وقد يتغير.
- GitHub يطلب مساحة ووقتًا محدودين للـ artifacts، لذلك نزّل APK واحتفظ به محليًا.
- APPIFY نفسه لا يستخدم API مدفوعة ولا يطلب اشتراكًا. لا يمكن ضمان حدود طرف ثالث أو تغييرها.

## فحص قبل البناء

شغّل:

```bash
python3 -m json.tool app/src/main/assets/app-config.json
./gradlew tasks
```

إذا كان الرابط غير صحيح أو غير HTTPS فسيرفضه التطبيق برسالة واضحة بدل فتحه.
