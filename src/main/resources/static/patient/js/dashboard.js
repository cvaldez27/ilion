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

    console.log("✅ PATIENT DASHBOARD");

})();