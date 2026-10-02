import type {SidebarsConfig} from '@docusaurus/plugin-content-docs';

const sidebars: SidebarsConfig = {
  docsSidebar: [
    'intro',
    {
      type: 'category',
      label: 'Getting Started',
      items: [
        'getting-started/installation',
        'getting-started/android-quickstart',
        'getting-started/ios-quickstart',
        'getting-started/migrating-to-2',
      ],
    },
    {
      type: 'category',
      label: 'Concepts',
      items: [
        'concepts/the-clock',
        'concepts/lessons-phases-and-takes',
        'concepts/styles-themes-and-scores',
      ],
    },
    {
      type: 'category',
      label: 'Guides',
      items: ['guides/licensing'],
    },
    {
      type: 'category',
      label: 'Components',
      items: [
        'components/features',
        'components/navigation',
        'components/charts-and-meters',
        'components/primitives-and-layouts',
      ],
    },
  ],
};

export default sidebars;
