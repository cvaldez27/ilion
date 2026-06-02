// dashboard.js - Versión corregida y mejorada
(function () {

    function byId(i) {
        return document.getElementById(i);
    }

    const HOME_TITLE = byId('pageTitle')?.textContent || 'Welcome';

    var TITLES = {
        home: HOME_TITLE,
        patients: 'Patients',
        archives: 'Archives',
        perfil: 'Perfil'
    };

    var MESES_ES = [
        'enero', 'febrero', 'marzo', 'abril', 'mayo', 'junio',
        'julio', 'agosto', 'septiembre', 'octubre', 'noviembre', 'diciembre'
    ];

    var MESES = [
        'ENERO', 'FEBRERO', 'MARZO', 'ABRIL', 'MAYO', 'JUNIO',
        'JULIO', 'AGOSTO', 'SEPTIEMBRE', 'OCTUBRE', 'NOVIEMBRE', 'DICIEMBRE'
    ];

    const today = new Date();

    var cy = today.getFullYear();
    var cm = today.getMonth();

    var selPat = null;
    var selPatId = null;
    var selDate = null;

    function toast(m) {
        var t = byId('toast');

        if (!t) return;

        t.textContent = m;
        t.classList.add('show');

        setTimeout(function () {
            t.classList.remove('show');
        }, 3000);
    }

    /* =========================
       Tabs
    ========================= */

    document.querySelectorAll('.tab[data-sec]').forEach(tab => {

        tab.addEventListener('click', () => {

            const k = tab.getAttribute('data-sec');

            document
                .querySelectorAll('.tab')
                .forEach(t => t.classList.remove('active'));

            document
                .querySelectorAll('.msec')
                .forEach(s => s.classList.remove('active'));

            tab.classList.add('active');

            const section = byId('sec-' + k);

            if (section) {
                section.classList.add('active');
            }

            const title = byId('pageTitle');

            if (title) {
                title.textContent = TITLES[k] || 'ÍLION';
            }
        });
    });

    /* =========================
       Medical Log
    ========================= */

    byId('btnLog')?.addEventListener('click', () => {

        byId('sboard')?.classList.toggle('open');

        byId('btnLog')?.classList.toggle('active-log');
    });

    /* =========================
       Calendario
    ========================= */

    function buildCal() {

        const mlbl = byId('spMonth');
        const table = byId('spCal');

        if (!mlbl || !table) return;

        const tbody = table.querySelector('tbody');

        if (!tbody) return;

        mlbl.textContent = MESES[cm] + ' ' + cy;

        tbody.innerHTML = '';

        const total = new Date(cy, cm + 1, 0).getDate();

        const fd = new Date(cy, cm, 1).getDay();

        const off = (fd === 0) ? 6 : fd - 1;

        for (let w = 0; w < 6; w++) {

            const rs = w * 7 - off + 1;

            if (rs > total) break;

            const tr = document.createElement('tr');

            for (let c = 0; c < 7; c++) {

                const dn = rs + c;

                const td = document.createElement('td');

                if (dn < 1 || dn > total) {

                    td.className = 'emp';
                    td.textContent = '';

                } else {

                    td.textContent = dn;

                    const dow = new Date(cy, cm, dn).getDay();

                    if (dow === 0 || dow === 6) {

                        td.classList.add('emp');

                    } else {

                        td.classList.add('has');

                        if (
                            selDate &&
                            dn === selDate.d &&
                            cm === selDate.m &&
                            cy === selDate.y
                        ) {
                            td.classList.add('picked');
                        }

                        td.addEventListener('click', function () {

                            table
                                .querySelectorAll('td')
                                .forEach(t => t.classList.remove('picked'));

                            this.classList.add('picked');

                            selDate = {
                                d: dn,
                                m: cm,
                                y: cy
                            };

                            checkSummary();
                        });
                    }
                }

                tr.appendChild(td);
            }

            tbody.appendChild(tr);
        }
    }

    byId('spPrev')?.addEventListener('click', () => {

        cm--;

        if (cm < 0) {
            cm = 11;
            cy--;
        }

        buildCal();
    });

    byId('spNext')?.addEventListener('click', () => {

        cm++;

        if (cm > 11) {
            cm = 0;
            cy++;
        }

        buildCal();
    });

    /* =========================
       Summary
    ========================= */

    function checkSummary() {

        if (!selPat || !selDate) return;

        const left = byId('spLeft');

        if (left) {
            left.style.display = 'none';
        }

        byId('spSum')?.classList.add('show');

        if (byId('spPatVal')) {
            byId('spPatVal').textContent = selPat;
        }

        if (byId('spDateVal')) {
            byId('spDateVal').textContent =
                selDate.d +
                ' de ' +
                MESES_ES[selDate.m] +
                ' de ' +
                selDate.y;
        }

        byId('btnConfirm')?.classList.add('show');
    }

    /* =========================
       Schedule Panel
    ========================= */

    function openSp() {

        byId('sp')?.classList.add('open');
        byId('spOv')?.classList.add('open');

        buildCal();
    }

    function closeSp() {

        byId('sp')?.classList.remove('open');
        byId('spOv')?.classList.remove('open');

        selPat = null;
        selPatId = null;
        selDate = null;

        const left = byId('spLeft');

        if (left) {
            left.style.display = '';
        }

        byId('spSum')?.classList.remove('show');
        byId('btnConfirm')?.classList.remove('show');

        byId('plist')
            ?.querySelectorAll('.pitem')
            .forEach(p => p.classList.remove('sel'));

        if (byId('spPatVal')) {
            byId('spPatVal').textContent = '—';
        }

        if (byId('spDateVal')) {
            byId('spDateVal').textContent = '—';
        }

        if (byId('spHH')) {
            byId('spHH').value = '16';
        }

        if (byId('spMM')) {
            byId('spMM').value = '00';
        }

        const desc = document.querySelector('.desc-ta');

        if (desc) {
            desc.value = '';
        }
    }

    /* =========================
       Buscar paciente
    ========================= */

    byId('psearch')?.addEventListener('input', function () {

        const q = this.value.toLowerCase();

        byId('plist')
            ?.querySelectorAll('.pitem')
            .forEach(d => {

                d.style.display =
                    d.textContent.toLowerCase().includes(q)
                        ? ''
                        : 'none';
            });
    });

    /* =========================
       Seleccionar paciente
    ========================= */

    byId('plist')?.addEventListener('click', function (e) {

        const item = e.target.closest('.pitem');

        if (!item) return;

        this.querySelectorAll('.pitem')
            .forEach(d => d.classList.remove('sel'));

        item.classList.add('sel');

        selPat = item.textContent.trim();
        selPatId = item.dataset.id;

        checkSummary();
    });

    /* =========================
   Confirmar cita
========================= */

byId('btnConfirm')?.addEventListener('click', function () {

    if (!selPatId) {
        toast('⚠️ Selecciona un paciente');
        return;
    }

    if (!selPat || !selDate) {
        toast('⚠️ Selecciona paciente y fecha');
        return;
    }

    const hh = (byId('spHH')?.value || '16').padStart(2, '0');
    const mm = (byId('spMM')?.value || '00').padStart(2, '0');

    const h = parseInt(hh, 10);
    const m = parseInt(mm, 10);

    if (
        isNaN(h) ||
        isNaN(m) ||
        h < 0 ||
        h > 23 ||
        m < 0 ||
        m > 59
    ) {
        toast('⚠️ Hora inválida');
        return;
    }

    const fecha =
        selDate.y + '-' +
        String(selDate.m + 1).padStart(2, '0') + '-' +
        String(selDate.d).padStart(2, '0');

    const hora = hh + ':' + mm;

    const descripcion =
        document.querySelector('.desc-ta')?.value || '';

    const form = document.createElement('form');

    form.method = 'POST';
    form.action = '/doctor/schedule';

    function addField(name, value) {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = name;
        input.value = value;
        form.appendChild(input);
    }

    addField('patientId', selPatId);
    addField('dateStr', fecha);
    addField('timeStr', hora);
    addField('description', descripcion);

    document.body.appendChild(form);

    form.submit();
});

        /*
        Aquí puedes enviar la información al backend:

        fetch('/appointments', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                patientId: selPatId,
                day: selDate.d,
                month: selDate.m + 1,
                year: selDate.y,
                hour: hh,
                minute: mm
            })
        });
        */
    

    /* =========================
       Eventos panel
    ========================= */

    byId('btnSched')?.addEventListener('click', openSp);
    byId('spClose')?.addEventListener('click', closeSp);
    byId('spMin')?.addEventListener('click', closeSp);
    byId('spOv')?.addEventListener('click', closeSp);

    console.log('✅ dashboard.js cargado correctamente');

})();