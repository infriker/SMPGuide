const fs = require('fs');
const path = require('path');

const SRC = 'C:/Users/Infriker/AndroidStudioProjects/SMPshpargalki.github.io-main/Калькуляторы';
const DEST = 'C:/Users/Infriker/AndroidStudioProjects/SMP_HELP/app/src/main/assets/calculators';

if (!fs.existsSync(DEST)) fs.mkdirSync(DEST, { recursive: true });

// ─── общий CSS (взят из styles.css) ───────────────────────────────────────
const CSS = `
:root{--bg:#fff;--text:#222;--panel:#fff;--sub:#f7f7f7;--border:#ccc;--shadow:rgba(0,0,0,.15);--red:#e63946;--blue:#4682B4;}
*{box-sizing:border-box;}
body{background:var(--bg);font-family:Arial,sans-serif;margin:0;padding:16px;color:var(--text);}
h2{font-size:20px;text-align:center;color:var(--red);border-bottom:2px solid var(--red);padding-bottom:8px;margin-bottom:16px;}
.calc-box{background:var(--panel);padding:16px;border-radius:12px;box-shadow:0 4px 10px var(--shadow);}
.calc-title{text-align:center;font-size:20px;margin-bottom:14px;}
.calc-row{display:flex;gap:10px;margin-bottom:12px;align-items:center;}
.calc-select{flex:1;padding:10px;font-size:15px;border-radius:8px;border:1px solid var(--border);background:var(--panel);color:var(--text);}
.calc-result{background:var(--sub);border-radius:8px;padding:14px;font-size:17px;text-align:center;color:var(--text);margin-top:10px;}
.calc-total{margin-top:12px;padding:12px;background:var(--sub);border:1px solid var(--border);border-radius:10px;font-size:18px;font-weight:bold;text-align:center;}
.calc-info{margin-top:10px;padding:10px;border-radius:8px;border:2px solid var(--red);color:var(--red);font-size:16px;font-weight:bold;text-align:center;background:var(--panel);}
/* calc-section grid rows */
.calc-section .calc-row{display:grid;grid-template-columns:55% 30% 15%;border:1px solid var(--border);border-radius:10px;overflow:hidden;margin-bottom:8px;background:var(--sub);}
.calc-section .calc-row>*{border-right:1px solid var(--border);padding:8px;min-width:0;}
.calc-section .calc-row>*:last-child{border-right:none;}
.calc-section .calc-row label{font-size:13px;line-height:1.2;display:flex;align-items:center;word-break:break-word;}
.calc-section .calc-row select{width:100%;padding:5px;font-size:13px;border-radius:6px;border:1px solid var(--border);background:var(--panel);color:var(--text);}
.calc-score{font-size:15px;font-weight:bold;text-align:center;display:flex;align-items:center;justify-content:center;}
/* pregnancy */
.preg-box{background:var(--panel);padding:14px;border-radius:12px;box-shadow:0 4px 10px var(--shadow);}
.preg-inner{background:var(--sub);padding:14px;border-radius:10px;}
.preg-box label{color:var(--text);font-size:15px;}
.preg-box input[type=date]{width:100%;padding:10px;margin:8px 0 14px;border:1px solid var(--border);border-radius:8px;background:var(--panel);color:var(--text);font-size:15px;}
.red-btn{background:var(--red);color:#fff;width:100%;padding:12px;border:none;border-radius:10px;font-size:17px;cursor:pointer;}
#pregResult{background:var(--panel);padding:14px;border-radius:10px;margin-top:14px;color:var(--text);font-size:17px;text-align:center;}
/* drugs */
.drug-search-row{display:flex;gap:6px;margin-bottom:10px;}
.calc-input{flex:1;padding:10px;border-radius:8px;border:1px solid var(--border);background:var(--panel);color:var(--text);font-size:15px;}
.list-btn{width:42px;background:#e0e0e0;border-radius:8px;border:none;font-size:18px;cursor:pointer;}
#drugList{font-size:17px;line-height:1.5;background:var(--panel);color:var(--text);border:1px solid var(--border);width:100%;}
`;

// ─── HTML skeleton для каждого калькулятора ────────────────────────────────
const CALCS = [
  {
    file: 'ДетскиеНормы.js',
    out:  'detskie_normy.html',
    title: 'Детские возрастные нормы',
    body: `<div class="calc-box">
<h3 class="calc-title">Расчёт примерных возрастных норм</h3>
<div class="calc-row">
  <select id="ageSelect" class="calc-select"><option value="">Выберите возраст</option></select>
  <select id="genderSelect" class="calc-select">
    <option value="">Пол</option>
    <option value="girl">Девочка</option>
    <option value="boy">Мальчик</option>
  </select>
</div>
<div id="normResult" class="calc-result">Выберите возраст и пол.</div>
</div>`
  },
  {
    file: 'ПрепаратыПедиатрия.js',
    out:  'prep_pediatriya.html',
    title: 'Препараты в педиатрии',
    body: `<div class="calc-box">
<div class="drug-search-row">
  <input id="drugSearch" class="calc-input" placeholder="Введите препарат...">
  <button id="drugListToggle" class="list-btn">🔽</button>
</div>
<select id="drugList" class="calc-select" size="6" style="display:none"></select>
<div id="drugInfo" class="calc-result" style="display:none;"></div>
<select id="drugParam" class="calc-select" style="display:none"></select>
<div id="drugResult" class="calc-result" style="display:none;"></div>
</div>`
  },
  {
    file: 'СрокБеременности.js',
    out:  'srok_beremennosti.html',
    title: 'Срок беременности, ПДР',
    body: `<div class="preg-box">
<div class="preg-inner">
  <label for="lmpDate">Дата последней менструации:</label>
  <input type="date" id="lmpDate">
  <button class="red-btn" id="calcPreg">Рассчитать</button>
  <div id="pregResult" style="display:none;">
    <p id="pregWeeks"></p>
    <p id="pregDue"></p>
    <p id="pregVDM"></p>
  </div>
</div>
</div>`
  },
  {
    file: 'ШОКС.js',
    out:  'shoks.html',
    title: 'Шкала ШОКС (ХСН)',
    body: `<div class="calc-section"><div class="calc-box">
<h3 class="calc-title">Шкала Оценки Клинического Состояния</h3>
<div id="shoxRows"></div>
<div class="calc-total">Общий балл: <span id="shoxTotal">0</span></div>
<div id="shoxInfo" class="calc-info" style="display:none;"></div>
</div></div>`
  },
  {
    file: 'NEWS.js',
    out:  'news.html',
    title: 'Шкала NEWS',
    body: `<div class="calc-section"><div class="calc-box">
<h3 class="calc-title">Протокол оценки тяжести состояния NEWS</h3>
<div id="newsRows"></div>
<div class="calc-total">Общий балл: <span id="newsTotal">0</span></div>
</div></div>`
  },
  {
    file: 'ГЛАЗГО.js',
    out:  'glazgo.html',
    title: 'Шкала комы Глазго',
    body: `<div class="calc-section"><div class="calc-box">
<h3 class="calc-title">Шкала комы Глазго</h3>
<div id="gcsRows"></div>
<div class="calc-total">Общий балл: <span id="gcsTotal">0</span></div>
<div id="gcsInfo" class="calc-info" style="display:none;"></div>
</div></div>`
  },
  {
    file: 'ТЭЛА.js',
    out:  'tela.html',
    title: 'Шкала вероятности ТЭЛА',
    body: `<div class="calc-section"><div class="calc-box">
<h3 class="calc-title">Шкала вероятности ТЭЛА (Wells)</h3>
<div id="wellsRows"></div>
<div class="calc-total">Общий балл: <span id="wellsTotal">0</span></div>
<div id="wellsInfo" class="calc-info" style="display:none;"></div>
</div></div>`
  },
  {
    file: 'LAMS.js',
    out:  'lams.html',
    title: 'Шкала LAMS',
    body: `<div class="calc-section"><div class="calc-box">
<h3 class="calc-title">Шкала моторного дефицита LAMS</h3>
<div id="strokeRows"></div>
<div class="calc-total">Общий балл: <span id="strokeTotal">0</span></div>
</div></div>`
  },
];

const kotlinLines = [];

for (const calc of CALCS) {
  const jsPath = path.join(SRC, calc.file);
  const jsCode = fs.readFileSync(jsPath, 'utf8');
  const outPath = path.join(DEST, calc.out);

  const html = `<!DOCTYPE html>
<html lang="ru">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>${calc.title}</title>
<style>${CSS}</style>
</head>
<body>
<h2>${calc.title}</h2>
${calc.body}
<script>
${jsCode}
</script>
</body>
</html>`;

  fs.writeFileSync(outPath, html, 'utf8');
  console.log(`Created: ${calc.out}`);
  kotlinLines.push(`        MenuItem("", "${calc.title}", "calculators/${calc.out}"),`);
}

console.log('\n// DataSource.kt — calculators list:');
console.log('    val calculators: List<MenuItem> = listOf(');
kotlinLines.forEach(l => console.log(l));
console.log('    )');
