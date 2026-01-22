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

## Quick Start

To see the plugin in action, follow these simple steps to create a demo graph.

**1. Add Dependencies**

Add the `saa-graph-composer` library and the required Spring AI Alibaba dependencies to your `pom.xml`:

```xml
<properties>
    <spring-ai-alibaba.version>1.1.0.0</spring-ai-alibaba.version>
    <saa-graph-composer.version>0.2.2</saa-graph-composer.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.alibaba.cloud.ai</groupId>
            <artifactId>spring-ai-alibaba-bom</artifactId>
            <version>${spring-ai-alibaba.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>cn.teacy.ai</groupId>
        <artifactId>saa-graph-composer</artifactId>
        <version>${saa-graph-composer.version}</version>
    </dependency>

    <dependency>
        <groupId>com.alibaba.cloud.ai</groupId>
        <artifactId>spring-ai-alibaba-graph-core</artifactId>
    </dependency>
</dependencies>
```

**2. Create a Graph Composer**

```java
import cn.teacy.ai.annotation.*;
import cn.teacy.ai.interfaces.CompileConfigSupplier;
import com.alibaba.cloud.ai.graph.CompileConfig;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.action.EdgeAction;
import com.alibaba.cloud.ai.graph.state.strategy.AppendStrategy;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@GraphComposer(description = "This is a demo graph composer")
public class LoopGraphComposer {
    private static final String NODE_PROCESS = "process";
    private static final String NODE_CHECK = "check";

    @GraphKey
    private static final String KEY_COUNT = "count";

    @GraphKey(strategy = AppendStrategy.class)
    private static final String KEY_LOGS = "logs";

    @GraphNode(id = NODE_PROCESS, isStart = true, next = NODE_CHECK, description = "append log with count")
    final AsyncNodeAction process = state -> {
        Integer count = (Integer) state.value(KEY_COUNT).orElse(0);
        return CompletableFuture.completedFuture(Map.of(
                KEY_LOGS, "loop-" + count
        ));
    };

    @GraphNode(id = NODE_CHECK, description = "check if count meets the limit 3")
    final AsyncNodeAction check = state -> {
        Integer count = (Integer) state.value(KEY_COUNT).orElse(0);
        return CompletableFuture.completedFuture(Map.of(KEY_COUNT, count + 1));
    };

    @ConditionalEdge(source = NODE_CHECK, routes = {NODE_PROCESS, StateGraph.END})
    final EdgeAction checkLoop = state -> {
        Integer count = (Integer) state.value(KEY_COUNT).orElse(0);
        return count < 3 ? NODE_PROCESS : StateGraph.END;
    };

    @GraphCompileConfig
    final CompileConfigSupplier config = () -> CompileConfig.builder().build();
}
```

**3. One-Click Preview**

Look for the **Preview icon** in the left gutter right beside the class definition. Click this icon to instantly open the **SAA Graph Preview** tool window and render the live diagram.

![Showcase](./assets/showcase.gif)

### Alternative Verification (Demo Project)

If you prefer to test with an existing project, you can clone the official framework repository, which contains extensive test cases compatible with this plugin. 

1. **Clone the repository:**  

   ```bash
   git clone https://github.com/chrisis58/saa-graph-composer.git
   ```

2. **Open in IntelliJ IDEA** and wait for Maven indexing to complete. 

3. **Navigate to test cases:**  Open `saa-graph-composer/src/test/java/cn/teacy/ai/tests` (or the specific path to your test files). 

4. **Preview:**  You will see the **Preview icon** in the gutter for any class annotated with `@GraphComposer`. Click it to visualize the test graph.
