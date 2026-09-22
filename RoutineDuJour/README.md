# Routine du jour

Widget Android 2×2 : fond rouge « Attention, tu n'as pas fait ta routine du jour »,
fond vert « Super, la routine du jour est faite ». Remise à zéro chaque nuit à minuit.

## Compiler avec GitHub Actions
1. Créer un dépôt GitHub (privé si tu veux) et y pousser ce dossier, `.github/` compris.
2. Onglet **Actions** → « Build APK » se lance tout seul (ou « Run workflow »).
3. Une fois vert : ouvrir l'exécution → section **Artifacts** → télécharger `RoutineDuJour-apk`
   (un zip qui contient `app-debug.apk`).

## Compiler avec Android Studio
File → Open → ce dossier, attendre la synchro Gradle, puis Build → Build App Bundle(s) / APK(s) → Build APK(s).
L'APK sort dans `app/build/outputs/apk/debug/app-debug.apk`.
