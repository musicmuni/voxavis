import {themes as prismThemes} from 'prism-react-renderer';
import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';

const config: Config = {
  title: 'VoxaVis',
  tagline: 'Pitch and singing visualization for Compose Multiplatform',

  future: {
    v4: true,
  },

  // Served from the public repository's GitHub Pages (the gh-pages branch,
  // written by `npm run deploy`).
  url: 'https://musicmuni.github.io',
  baseUrl: '/voxavis/',
  organizationName: 'musicmuni',
  projectName: 'voxavis',
  trailingSlash: false,

  onBrokenLinks: 'throw',

  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      {
        docs: {
          sidebarPath: './sidebars.ts',
          routeBasePath: '/', // Docs at root, no /docs prefix
        },
        blog: false,
        theme: {
          customCss: './src/css/custom.css',
        },
      } satisfies Preset.Options,
    ],
  ],

  themes: [
    [
      '@easyops-cn/docusaurus-search-local',
      {
        hashed: true,
        language: ['en'],
        docsRouteBasePath: '/',
        highlightSearchTermsOnTargetPage: true,
        explicitSearchResultPath: true,
      },
    ],
  ],

  themeConfig: {
    colorMode: {
      defaultMode: 'dark',
      disableSwitch: true,
      respectPrefersColorScheme: false,
    },
    navbar: {
      title: 'VoxaVis',
      items: [
        {
          type: 'docSidebar',
          sidebarId: 'docsSidebar',
          position: 'left',
          label: 'Docs',
        },
        {
          href: 'https://github.com/musicmuni/voxavis/blob/main/CHANGELOG.md',
          position: 'left',
          label: 'Changelog',
        },
        {
          href: 'https://github.com/musicmuni/voxavis',
          label: 'GitHub',
          position: 'right',
        },
      ],
    },
    footer: {
      style: 'dark',
      links: [
        {
          title: 'Docs',
          items: [
            {label: 'Getting Started', to: '/'},
            {label: 'Migrating to 2.0', to: '/getting-started/migrating-to-2'},
          ],
        },
        {
          title: 'More',
          items: [
            {label: 'GitHub', href: 'https://github.com/musicmuni/voxavis'},
            {label: 'VoxaTrace', href: 'https://voxatrace.ai'},
          ],
        },
      ],
      copyright: `Copyright © ${new Date().getFullYear()} MusicMuni.`,
    },
    prism: {
      theme: prismThemes.github,
      darkTheme: prismThemes.dracula,
      additionalLanguages: ['kotlin', 'swift', 'groovy'],
    },
  } satisfies Preset.ThemeConfig,
};

export default config;
