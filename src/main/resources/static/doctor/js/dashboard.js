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

    // --- Lógica para la sección de Pacientes ---

let currentPatientId = null;

function showPatientView(viewId, title) {
    const patientViews = ['v-list', 'v-new', 'v-detail'];
    patientViews.forEach(v => {
        const el = byId(v);
        if (el) el.style.display = 'none';
    });

    const targetView = byId(viewId);
    if (targetView) targetView.style.display = 'block';

    const titleEl = document.querySelector('#sec-patients #pageTitle');
    if (titleEl) titleEl.textContent = title || 'Patients';
}

function setPersonalInfoReadonly(readonly) {
    [
        'pdName',
        'pdLastName',
        'pdEmail',
        'pdPhone',
        'pdDOB',
        'pdBloodType',
        'pdAllergies',
        'pdChronicConditions',
        'pdDiabetes',
        'pdHypertension',
        'pdCancer',
        'pdOtherFH'
    ].forEach(id => {
        const el = byId(id);
        if (el) el.readOnly = readonly;
    });
}

function loadPatientDetails(patient) {
    currentPatientId = patient.id;

    byId('pdNameText').textContent =
        `${patient.name || ''} ${patient.lastName || ''}`;

    byId('pdName').value = patient.name || '';
    byId('pdLastName').value = patient.lastName || '';
    byId('pdEmail').value = patient.email || '';
    byId('pdPhone').value = patient.phone || '';
    byId('pdDOB').value = patient.dateOfBirth || '';
    byId('pdBloodType').value = patient.bloodType || '';
    byId('pdAllergies').value = patient.allergies || '';
    byId('pdChronicConditions').value = patient.chronicConditions || '';
    byId('pdMedications').value = 'No data';

    byId('pdDiabetes').value = patient.familyHistory || '';
    byId('pdHypertension').value = patient.familyHistory || '';
    byId('pdCancer').value = patient.cancerHistory || '';
    byId('pdOtherFH').value = patient.geneticDisorders || '';

    setPersonalInfoReadonly(true);

    if (byId('btnEditInfo')) {
        byId('btnEditInfo').style.display = 'inline-block';
    }

    if (byId('btnSaveInfo')) {
        byId('btnSaveInfo').style.display = 'none';
    }

    loadPrescriptions(patient.id);
    loadProgressNotes(patient.id);
}

function loadPrescriptions(patientId) {
    fetch(`/doctor/patient/${patientId}/prescriptions`)
        .then(res => res.json())
        .then(data => {
            const list = byId('medicationList');
            if (!list) return;

            if (!data || data.length === 0) {
                list.innerHTML = '<p style="color:#999;font-size:13px;">No prescriptions found.</p>';
                return;
            }

            list.innerHTML = data.map(p => `
                <div class="med-item">
                    <span class="med-name">${p.medications || '—'}</span>
                    <span class="med-dose">${p.dosage || '—'}</span>
                    <span class="med-start">${p.date || '—'}</span>
                    <p style="width:100%;font-size:12px;color:#666;">
                        <strong>Diagnosis:</strong> ${p.diagnosis || '—'}<br>
                        <strong>Instructions:</strong> ${p.instructions || '—'}<br>
                        <strong>Notes:</strong> ${p.notes || '—'}
                    </p>
                </div>
            `).join('');
        })
        .catch(err => {
            console.error(err);
            toast('Error loading prescriptions');
        });
}

function loadProgressNotes(patientId) {
    fetch(`/doctor/patient/${patientId}/progress-notes`)
        .then(res => res.json())
        .then(data => {
            const list = byId('progressNotesList');
            if (!list) return;

            if (!data || data.length === 0) {
                list.innerHTML = '<p style="color:#999;font-size:13px;">No progress notes found.</p>';
                return;
            }

            list.innerHTML = data.map(n => `
                <div class="note-item">
                    <div class="note-header">
                        <span class="note-date">${n.date || '—'}</span>
                        <span class="note-author">Doctor</span>
                    </div>
                    <div class="note-body">
                        <p><strong>Subjective:</strong> ${n.subjective || '—'}</p>
                        <p><strong>Objective:</strong> ${n.objective || '—'}</p>
                        <p><strong>Assessment:</strong> ${n.assessment || '—'}</p>
                        <p><strong>Plan:</strong> ${n.plan || '—'}</p>
                    </div>
                </div>
            `).join('');
        })
        .catch(err => {
            console.error(err);
            toast('Error loading progress notes');
        });
}

byId('btnCloseNew')?.addEventListener('click', function () {
    showPatientView('v-list', 'Mine patients');
});

document.querySelector('#sec-patients')?.addEventListener('click', function (e) {
    if (!e.target.classList.contains('access-link')) return;

    e.preventDefault();

    const patientId =
        e.target.dataset.id ||
        e.target.closest('tr')?.dataset.id;

    if (!patientId) {
        toast('No patient ID found');
        return;
    }

    fetch(`/doctor/patient/${patientId}`)
        .then(response => {
            if (!response.ok) throw new Error('Patient not found');
            return response.json();
        })
        .then(patient => {
            loadPatientDetails(patient);

            showPatientView(
                'v-detail',
                `${patient.name || ''} ${patient.lastName || ''}`
            );

            document
                .querySelectorAll('#sec-patients .pd-sec')
                .forEach(s => s.classList.remove('active'));

            document
                .querySelectorAll('#sec-patients .pd-link')
                .forEach(b => b.classList.remove('active'));

            byId('pd-personal')?.classList.add('active');

            document
                .querySelector('#sec-patients .pd-link[data-pd="pd-personal"]')
                ?.classList.add('active');
        })
        .catch(error => {
            console.error(error);
            toast('Error loading patient');
        });
});

byId('btnCloseDetail')?.addEventListener('click', function () {
    showPatientView('v-list', 'Mine patients');
});

document.querySelectorAll('#sec-patients .pd-link').forEach(btn => {
    btn.addEventListener('click', function () {
        const target = this.getAttribute('data-pd');

        document
            .querySelectorAll('#sec-patients .pd-sec')
            .forEach(s => s.classList.remove('active'));

        document
            .querySelectorAll('#sec-patients .pd-link')
            .forEach(b => b.classList.remove('active'));

        byId(target)?.classList.add('active');
        this.classList.add('active');
    });
});

byId('btnEditInfo')?.addEventListener('click', function () {
    setPersonalInfoReadonly(false);

    if (byId('btnEditInfo')) {
        byId('btnEditInfo').style.display = 'none';
    }

    if (byId('btnSaveInfo')) {
        byId('btnSaveInfo').style.display = 'inline-block';
    }
});

byId('btnSaveInfo')?.addEventListener('click', function () {
    if (!currentPatientId) {
        toast('No patient selected');
        return;
    }

    const body = new URLSearchParams({
        name: byId('pdName')?.value || '',
        lastName: byId('pdLastName')?.value || '',
        email: byId('pdEmail')?.value || '',
        phone: byId('pdPhone')?.value || '',
        dateOfBirth: byId('pdDOB')?.value || '',
        bloodType: byId('pdBloodType')?.value || '',
        allergies: byId('pdAllergies')?.value || '',
        chronicConditions: byId('pdChronicConditions')?.value || '',
        familyHistory: byId('pdDiabetes')?.value || '',
        cancerHistory: byId('pdCancer')?.value || '',
        geneticDisorders: byId('pdOtherFH')?.value || ''
    });

    fetch(`/doctor/patient/${currentPatientId}/update`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: body
    })
        .then(res => {
            if (!res.ok) throw new Error('Error updating patient');
            return res.json();
        })
        .then(patient => {
            loadPatientDetails(patient);
            toast('✅ Patient information updated');
        })
        .catch(err => {
            console.error(err);
            toast('Error saving patient information');
        });
});

byId('btnSavePrescription')?.addEventListener('click', function () {
    if (!currentPatientId) {
        toast('No patient selected');
        return;
    }

    const body = new URLSearchParams({
        diagnosis: byId('rxDiagnosis')?.value || '',
        medications: byId('rxMedications')?.value || '',
        dosage: byId('rxDosage')?.value || '',
        instructions: byId('rxInstructions')?.value || '',
        notes: byId('rxNotes')?.value || ''
    });

    fetch(`/doctor/patient/${currentPatientId}/prescription`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: body
    })
        .then(res => {
            if (!res.ok) throw new Error('Error saving prescription');
            return res.json();
        })
        .then(() => {
            toast('✅ Prescription saved');

            ['rxDiagnosis', 'rxMedications', 'rxDosage', 'rxInstructions', 'rxNotes']
                .forEach(id => {
                    const el = byId(id);
                    if (el) el.value = '';
                });

            loadPrescriptions(currentPatientId);
        })
        .catch(err => {
            console.error(err);
            toast('Error saving prescription');
        });
});

byId('btnSaveProgressNote')?.addEventListener('click', function () {
    if (!currentPatientId) {
        toast('No patient selected');
        return;
    }

    const body = new URLSearchParams({
        subjective: byId('pnSubjective')?.value || '',
        objective: byId('pnObjective')?.value || '',
        assessment: byId('pnAssessment')?.value || '',
        plan: byId('pnPlan')?.value || ''
    });

    fetch(`/doctor/patient/${currentPatientId}/progress-note`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: body
    })
        .then(res => {
            if (!res.ok) throw new Error('Error saving progress note');
            return res.json();
        })
        .then(() => {
            toast('✅ Progress note saved');

            ['pnSubjective', 'pnObjective', 'pnAssessment', 'pnPlan']
                .forEach(id => {
                    const el = byId(id);
                    if (el) el.value = '';
                });

            loadProgressNotes(currentPatientId);
        })
        .catch(err => {
            console.error(err);
            toast('Error saving progress note');
        });
});

byId('btnPhoto')?.addEventListener('click', function () {
    byId('photoInput')?.click();
});

byId('photoInput')?.addEventListener('change', function () {
    if (this.files && this.files[0]) {
        toast('📷 ' + this.files[0].name);
    }
});

byId('btnFinished')?.addEventListener('click', function () {
    const name = byId('npName')?.value.trim();

    if (!name) {
        toast('⚠️ Name is required');
        return;
    }

    toast('✅ Patient saved: ' + name);

    document
        .querySelectorAll('#sec-patients .np-inp')
        .forEach(inp => inp.value = '');

    showPatientView('v-list', 'Mine patients');
});

/* =========================
   ARCHIVES
========================= */

let arcPatientId = null;
let arcPatientName = null;
let arcSelectedCategory = 'RADIOGRAPHS';
let arcPendingFile = null;
let arcFiles = [];

const ARC_CATEGORIES = [
    { key: 'RADIOGRAPHS', label: 'Radiografías', icon: '🫁' },
    { key: 'LABORATORIES', label: 'Laboratorios', icon: '🧪' },
    { key: 'STUDIES', label: 'Estudios', icon: '📋' },
    { key: 'ANALYSES', label: 'Análisis', icon: '🔬' },
    { key: 'EXAMS', label: 'Exámenes', icon: '📄' }
];

function arcFmtSize(bytes) {
    if (!bytes) return '—';
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1048576) return (bytes / 1024).toFixed(0) + ' KB';
    return (bytes / 1048576).toFixed(1) + ' MB';
}

function arcResetDropZone() {
    const txt = byId('arcDzTxt');
    const sub = byId('arcDzSub');
    const input = byId('arcFileInput');

    if (txt) txt.textContent = 'Drag & drop your PDF here';
    if (sub) sub.textContent = 'or click to browse your device';
    if (input) input.value = '';

    arcPendingFile = null;
}

function arcRenderCategories() {
    const tabs = byId('arcCategoryTabs');
    if (!tabs) return;

    tabs.innerHTML = '';

    ARC_CATEGORIES.forEach(cat => {
        const count = arcFiles.filter(f => f.category === cat.key).length;

        const btn = document.createElement('button');
        btn.className = 'ctab' + (arcSelectedCategory === cat.key ? ' act' : '');
        btn.innerHTML =
            `${cat.icon} ${cat.label}` +
            (count ? ` <small style="opacity:.65">(${count})</small>` : '');

        btn.addEventListener('click', function () {
            arcSelectedCategory = cat.key;
            arcRenderCategories();
            arcRenderFiles();
        });

        tabs.appendChild(btn);
    });
}

function arcRenderFiles() {
    const grid = byId('arcFileGrid');
    if (!grid) return;

    grid.innerHTML = '';

    const filtered = arcFiles.filter(f => f.category === arcSelectedCategory);

    if (!filtered.length) {
        const empty = document.createElement('div');
        empty.className = 'f-empty';
        empty.textContent = 'No PDF files in this category yet.';
        grid.appendChild(empty);
        return;
    }

    filtered.forEach(file => {
        const item = document.createElement('div');
        item.className = 'f-item';

        item.innerHTML = `
            <div class="f-ico">📄</div>
            <div class="f-inf">
                <div class="f-nm">${file.fileName || 'PDF file'}</div>
                <div class="f-meta">${file.date || '—'} · PDF</div>
            </div>
            <button class="f-del" title="Delete">🗑️</button>
        `;

        item.addEventListener('click', function () {
            window.open(`/doctor/file/${file.id}`, '_blank');
        });

        item.querySelector('.f-del').addEventListener('click', function (e) {
            e.stopPropagation();
            arcDeleteFile(file.id);
        });

        grid.appendChild(item);
    });
}

function arcLoadFiles(patientId) {
    fetch(`/doctor/patient/${patientId}/files`)
        .then(res => {
            if (!res.ok) throw new Error('Error loading files');
            return res.json();
        })
        .then(files => {
            arcFiles = files || [];
            arcRenderCategories();
            arcRenderFiles();
        })
        .catch(err => {
            console.error(err);
            toast('Error loading patient files');
        });
}

function arcSelectPatient(patientId, patientName) {
    arcPatientId = patientId;
    arcPatientName = patientName;
    arcSelectedCategory = 'RADIOGRAPHS';
    arcPendingFile = null;

    document
        .querySelectorAll('#sec-archives .p-item')
        .forEach(p => p.classList.remove('sel'));

    const selected = document.querySelector(`#sec-archives .p-item[data-id="${patientId}"]`);
    if (selected) selected.classList.add('sel');

    if (byId('arcEmpty')) {
        byId('arcEmpty').style.display = 'none';
    }

    if (byId('arcFA')) {
        byId('arcFA').classList.add('open');
    }

    if (byId('arcPN')) {
        byId('arcPN').textContent = patientName;
    }

    if (byId('arcUploadPanel')) {
        byId('arcUploadPanel').classList.remove('open');
    }

    if (byId('arcBtnUpload')) {
        byId('arcBtnUpload').style.display = '';
    }

    arcResetDropZone();
    arcLoadFiles(patientId);
}

function arcUploadFile() {
    if (!arcPatientId) {
        toast('Select a patient first');
        return;
    }

    if (!arcPendingFile) {
        toast('⚠️ Select a PDF first');
        return;
    }

    if (arcPendingFile.type !== 'application/pdf') {
        toast('⚠️ Only PDF files are allowed');
        return;
    }

    const formData = new FormData();
    formData.append('category', byId('arcUploadCategory')?.value || 'RADIOGRAPHS');
    formData.append('file', arcPendingFile);

    fetch(`/doctor/patient/${arcPatientId}/files`, {
        method: 'POST',
        body: formData
    })
        .then(res => {
            if (!res.ok) throw new Error('Error uploading file');
            return res.json();
        })
        .then(() => {
            toast('✅ PDF uploaded');

            if (byId('arcUploadPanel')) {
                byId('arcUploadPanel').classList.remove('open');
            }

            if (byId('arcBtnUpload')) {
                byId('arcBtnUpload').style.display = '';
            }

            arcResetDropZone();
            arcLoadFiles(arcPatientId);
        })
        .catch(err => {
            console.error(err);
            toast('Error uploading PDF');
        });
}

function arcDeleteFile(fileId) {
    fetch(`/doctor/file/${fileId}`, {
        method: 'DELETE'
    })
        .then(res => {
            if (!res.ok) throw new Error('Error deleting file');
            return res.text();
        })
        .then(() => {
            toast('🗑️ PDF deleted');
            arcLoadFiles(arcPatientId);
        })
        .catch(err => {
            console.error(err);
            toast('Error deleting PDF');
        });
}

byId('arcPatientSearch')?.addEventListener('input', function () {
    const q = this.value.toLowerCase();

    document
        .querySelectorAll('#sec-archives .p-item')
        .forEach(item => {
            const name = item.dataset.name?.toLowerCase() || '';
            item.style.display = name.includes(q) ? '' : 'none';
        });
});

document.querySelector('#arcPatientList')?.addEventListener('click', function (e) {
    const item = e.target.closest('.p-item');
    if (!item) return;

    const patientId = item.dataset.id;
    const patientName = item.dataset.name || 'Patient';

    arcSelectPatient(patientId, patientName);
});

byId('arcBtnUpload')?.addEventListener('click', function () {
    if (!arcPatientId) {
        toast('Select a patient first');
        return;
    }

    byId('arcUploadPanel')?.classList.add('open');

    if (byId('arcBtnUpload')) {
        byId('arcBtnUpload').style.display = 'none';
    }

    if (byId('arcUploadCategory')) {
        byId('arcUploadCategory').value = arcSelectedCategory;
    }
});

byId('arcBtnCancel')?.addEventListener('click', function () {
    byId('arcUploadPanel')?.classList.remove('open');

    if (byId('arcBtnUpload')) {
        byId('arcBtnUpload').style.display = '';
    }

    arcResetDropZone();
});

byId('arcBtnSave')?.addEventListener('click', arcUploadFile);

byId('arcDropZone')?.addEventListener('click', function () {
    byId('arcFileInput')?.click();
});

byId('arcDropZone')?.addEventListener('dragover', function (e) {
    e.preventDefault();
    this.classList.add('over');
});

byId('arcDropZone')?.addEventListener('dragleave', function () {
    this.classList.remove('over');
});

byId('arcDropZone')?.addEventListener('drop', function (e) {
    e.preventDefault();
    this.classList.remove('over');

    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
        arcHandleSelectedFile(e.dataTransfer.files[0]);
    }
});

byId('arcFileInput')?.addEventListener('change', function () {
    if (this.files && this.files[0]) {
        arcHandleSelectedFile(this.files[0]);
    }
});

function arcHandleSelectedFile(file) {
    if (file.type !== 'application/pdf') {
        toast('⚠️ Only PDF files are allowed');
        arcResetDropZone();
        return;
    }

    arcPendingFile = file;

    if (byId('arcDzTxt')) {
        byId('arcDzTxt').textContent = '📎 ' + file.name;
    }

    if (byId('arcDzSub')) {
        byId('arcDzSub').textContent = arcFmtSize(file.size);
    }
}

console.log("✅ DASHBOARD JS NUEVO 2026-06-02");
})();