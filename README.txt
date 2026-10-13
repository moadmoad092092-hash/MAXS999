APPIFY — تحويل موقع HTTPS إلى تطبيق Android حقيقي

هذا الإصدار لا يكتفي ببطاقة أو اختصار ويب: يحتوي على مشروع Android كامل داخل مجلد android-app، مكتوب بـ Kotlin ويستخدم WebView، مع Gradle وGitHub Actions لبناء APK من الهاتف.

المكونات:
- index.html / style.css / app.js: واجهة عربية RTL لاختيار الاسم والرابط واللون وحرف الأيقونة وتنزيل app-config.json.
- android-app/: مشروع Android قابل للبناء.
- android-app/app/src/main/assets/app-config.json: إعداد التطبيق والرابط.
- android-app/.github/workflows/build-apk.yml: بناء APK مجاني عند تشغيله من GitHub Actions.

الطريقة الموصى بها:
1) افتح index.html أو انشر المجلد على استضافة HTTPS.
2) أدخل اسم التطبيق ورابطًا يبدأ بـ https:// واختر اللون والحرف.
3) نزّل app-config.json واستبدله داخل android-app/app/src/main/assets/.
4) ارفع محتويات android-app إلى مستودع GitHub.
5) من Actions شغّل Build APPIFY APK.
6) بعد نجاح البناء فقط، نزّل Artifact باسم app-debug-apk وثبّت APK على الهاتف.

مهم: لا يحتوي الأرشيف على APK جاهز؛ لم يتم الادعاء ببنائه. البناء يتم فعليًا عبر Gradle في GitHub Actions. حدود الدقائق والمساحة تتبع سياسة GitHub المجانية وقد تتغير؛ لا توجد API مدفوعة أو اشتراك إلزامي.

راجع android-app/README.md للتفاصيل وخطوات التثبيت والفحص.
