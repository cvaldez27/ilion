(function () {

    function byId(id) {
        return document.getElementById(id);
    }

    const HOME_TITLE =
        byId('pageTitle')?.textContent || 'Welcome';

    const TITLES = {
        home: HOME_TITLE,
        archives: 'Archives',
        perfil: 'Profile'
    };

    document.querySelectorAll('.tab[data-sec]')
        .forEach(tab => {

            tab.addEventListener('click', () => {

                const key =
                    tab.getAttribute('data-sec');

                document
                    .querySelectorAll('.tab')
                    .forEach(t => t.classList.remove('active'));

                document
                    .querySelectorAll('.msec')
                    .forEach(s => s.classList.remove('active'));

                tab.classList.add('active');

                byId('sec-' + key)
                    ?.classList.add('active');

                byId('pageTitle').textContent =
                    TITLES[key];

                if (key === 'archives') {
                    loadArchives();
                }

                if (key === 'perfil') {
                    loadPrescriptions();
                    loadProgressNotes();
                }
            });

        });

    async function loadArchives() {

        try {

            const response =
                await fetch('/patient/files');

            const files =
                await response.json();

            const container =
                byId('archivesContainer');

            if (!container) return;

            if (!files.length) {

                container.innerHTML = `
                    <div class="empty-state">
                        No medical files uploaded yet
                    </div>
                `;

                return;
            }

            container.innerHTML = files.map(file => `

                <div class="archive-card">

                    <div class="archive-title">
                        ${file.fileName}
                    </div>

                    <div class="archive-meta">
                        Category: ${file.category}
                    </div>

                    <div class="archive-meta">
                        Date: ${file.date}
                    </div>

                    <a
                        href="/patient/file/${file.id}"
                        target="_blank"
                        class="archive-btn">

                        Open PDF

                    </a>

                </div>

            `).join('');

        } catch (error) {

            console.error(error);

        }
    }

    /* =========================
       PROFILE NAVIGATION
    ========================= */

    document
        .querySelectorAll('.patient-profile-link')
        .forEach(btn => {

            btn.addEventListener('click', function () {

                const target =
                    this.dataset.patientProfile;

                document
                    .querySelectorAll('.patient-profile-link')
                    .forEach(b => b.classList.remove('active'));

                document
                    .querySelectorAll('.patient-profile-sec')
                    .forEach(sec => sec.classList.remove('active'));

                this.classList.add('active');

                byId(target)
                    ?.classList.add('active');
            });

        });

    /* =========================
       MEDICAL HISTORY
    ========================= */

    async function loadPrescriptions() {

        try {

            const response =
                await fetch('/patient/prescriptions');

            const prescriptions =
                await response.json();

            const container =
                byId('patientPrescriptionsList');

            if (!container) return;

            if (!prescriptions.length) {

                container.innerHTML = `
                    <div class="empty-state">
                        No medical history available
                    </div>
                `;

                return;
            }

            container.innerHTML =
                prescriptions.map(p => `

                    <div class="patient-history-card">

                        <div class="patient-history-title">
                            ${p.diagnosis || 'No diagnosis'}
                        </div>

                        <div class="patient-history-date">
                            ${p.date || ''}
                        </div>

                        <div class="patient-history-text">
                            <strong>Medication:</strong>
                            ${p.medications || '-'}
                        </div>

                        <div class="patient-history-text">
                            <strong>Dosage:</strong>
                            ${p.dosage || '-'}
                        </div>

                        <div class="patient-history-text">
                            <strong>Instructions:</strong>
                            ${p.instructions || '-'}
                        </div>

                        <div class="patient-history-text">
                            <strong>Notes:</strong>
                            ${p.notes || '-'}
                        </div>

                    </div>

                `).join('');

        } catch (error) {

            console.error(error);

        }
    }

    /* =========================
       PROGRESS NOTES
    ========================= */

    async function loadProgressNotes() {

        try {

            const response =
                await fetch('/patient/progress-notes');

            const notes =
                await response.json();

            const container =
                byId('patientProgressNotesList');

            if (!container) return;

            if (!notes.length) {

                container.innerHTML = `
                    <div class="empty-state">
                        No progress notes available
                    </div>
                `;

                return;
            }

            container.innerHTML =
                notes.map(n => `

                    <div class="patient-history-card">

                        <div class="patient-history-date">
                            ${n.date || ''}
                        </div>

                        <div class="patient-history-text">
                            <strong>Subjective:</strong>
                            ${n.subjective || '-'}
                        </div>

                        <div class="patient-history-text">
                            <strong>Objective:</strong>
                            ${n.objective || '-'}
                        </div>

                        <div class="patient-history-text">
                            <strong>Assessment:</strong>
                            ${n.assessment || '-'}
                        </div>

                        <div class="patient-history-text">
                            <strong>Plan:</strong>
                            ${n.plan || '-'}
                        </div>

                    </div>

                `).join('');

        } catch (error) {

            console.error(error);

        }
    }

    console.log("✅ PATIENT DASHBOARD");

})();