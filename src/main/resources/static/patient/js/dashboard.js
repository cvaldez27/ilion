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
            });

        });

    console.log("✅ PATIENT DASHBOARD");

})();