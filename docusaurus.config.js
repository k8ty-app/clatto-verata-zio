/** @type {import('@docusaurus/types').DocusaurusConfig} */
module.exports = {
    title: 'Clatto... Verata... ZIO!',
    tagline: 'It\'s definitely a Z-word...',
    url: 'https://clatto.verataz.io',
    baseUrl: '/',
    onBrokenLinks: 'throw',
    onBrokenMarkdownLinks: 'warn',
    favicon: 'img/favicon.ico',
    organizationName: 'k8ty-app',
    projectName: 'clatto-verata-zio',
    themeConfig: {
        prism: {
            additionalLanguages: ['java', 'scala'],
            theme: require('prism-react-renderer/themes/dracula'),
        },
        colorMode: {
            defaultMode: 'dark',
            respectPrefersColorScheme: true,
        },
        navbar: {
            title: 'CVZ',
            logo: {
                alt: 'CVZ Logo',
                src: 'img/logo.svg',
            },
            items: [
                {
                    type: 'doc',
                    docId: 'intro',
                    position: 'left',
                    label: 'Examples',
                },
                {to: '/blog', label: 'Blog', position: 'left'},
                {to: '/about', label: 'About', position: 'left'},
                {
                    href: 'https://github.com/k8ty-app/clatto-verata-zio',
                    label: 'GitHub',
                    position: 'right',
                },
            ],
        },
        footer: {
            style: 'dark',
        },
    },
    presets: [
        [
            '@docusaurus/preset-classic',
            {
                docs: {
                    sidebarPath: require.resolve('./sidebars.js'),
                    // Please change this to your repo.
                    editUrl:
                        'https://github.com/k8ty-app/clatto-verata-zio/edit/main/',
                },
                blog: {
                    showReadingTime: true,
                    // Please change this to your repo.
                    editUrl:
                        'https://github.com/k8ty-app/clatto-verata-zio/edit/main/blog/',
                },
                theme: {
                    customCss: require.resolve('./src/css/custom.css'),
                },
            },
        ],
    ],
};
