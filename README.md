# saa-graph-composer-idea-plugin

![Build](https://github.com/chrisis58/saa-graph-composer-idea-plugin/workflows/Build/badge.svg) [![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID) [![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)

<!-- Plugin description -->
The official IntelliJ IDEA plugin for the [SAA Graph Composer](https://github.com/chrisis58/saa-graph-composer) framework.

This plugin is designed to supercharge your development with **Spring AI Alibaba Graph**. It provides seamless visualization and code insight support for your graph definitions.

### Key Features
* **Live Mermaid Preview:** Instantly visualize your `@GraphComposer` classes as interactive Mermaid flowcharts directly in the IDE side panel.
* **Smart Code Insight:** Implements `ImplicitUsageProvider` to recognize graph components, preventing false "Unused declaration" warnings for your composer classes.
* **Theme Synchronization:** The preview automatically adapts to your IDE's theme (Light/Dark/Darcula) for a native look and feel.
* **One-Click Export:** Easily copy the generated Mermaid source code for documentation or sharing.

*Note: This plugin requires the saa-graph-composer library to function.*
<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "saa-graph-composer-idea-plugin"</kbd> >
  <kbd>Install</kbd>

- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/chrisis58/saa-graph-composer-idea-plugin/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>
