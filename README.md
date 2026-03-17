# SMPGuide

Android-spravochnik dlya rabotnikov skoroj medicinskoj pomoshchi.

Soderzhit shablony kart vyzova i shpargalki po medicinskym temam. Rabotaet polnost'yu offlajn — ves' kontent hranitsya lokal'no v prilozhenii.

## Funkcional'

- 16 kategorij shablonov kart vyzova (akusherstvo, kardiologiya, nevrologiya, travmatologiya i dr.)
- 26 shpargalok (detskie dozy, parametry IVL, reanimaciya, shkaly i tablicy i dr.)
- Drawer-navigaciya s razdhelami: Shablony kart, Shpargalki, O prilozhenii
- Polnost'yu offlajn — ne trebuet podklyucheniya k internetu
- Medicinskaya tema oformleniya (krasnyj/belyj)

## Trebovaniya

- Android 7.0+ (API 24)
- Android Studio Narwhal (2025.1) ili novee

## Struktura proekta

```
app/src/main/
  assets/
    templates/      — HTML-fajly shablonov kart (16 kategorij)
    cheatsheets/    — HTML-fajly shpargalok (26 statej)
  java/com/example/smp_help/
    MainActivity.kt           — Drawer-navigaciya
    WebViewActivity.kt        — Prosmotr HTML-kontenta
    adapter/MenuItemAdapter.kt — Adapter dlya spiskov
    data/MenuItem.kt          — Model' dannyh
    data/DataSource.kt        — Spisok vseh shablonov i shpargalok
    ui/TemplatesFragment.kt   — Ekran shablonov kart
    ui/CheatSheetsFragment.kt — Ekran shpargalok
    ui/AboutFragment.kt       — Ekran "O prilozhenii"
```

## Kak dobavit' kontent

Zamenyajte HTML-fajly v papkah `app/src/main/assets/templates/` i `app/src/main/assets/cheatsheets/` na fajly s real'nym soderzhimym. Kazhdyj fajl — obychnyj HTML s lyubym formatirovaraniem (tablicy, spiski, zhirnyj tekst i t.d.).

## Sborka

1. Otkrojte proekt v Android Studio
2. Sync Gradle
3. Run na ustrojstve ili emulyatore

## Licenziya

Prilozhenie sozdano dlya udobstva rabotnikov SMP. Vsya medicinskaya informaciya vzyata iz otkrytyh istochnikov.
