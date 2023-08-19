# doc-l10n-kit

[![Actions Status](https://github.com/doc-l10n-kit/doc-l10n-kit/workflows/CI/badge.svg)](https://github.com/doc-l10n-kit/doc-l10n-kit/actions)

doc-l10n-kit is a set of utilities to translate .asciidoc files.

This project uses Quarkus, the Supersonic Subatomic Java Framework.
If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Build

### Prerequisites

- JDK 11
- GraalVM (If you want to build a native image directly)
- Docker or Podman (If you want to build a native image in a container)

### Creating an uber-jar


The application can be packaged using following command:

```
./gradlew quarkusBuild -Dquarkus.package.type=uber-jar
```

It produces the `doc-l10n-kit-runner.jar` file in the `build` directory.

## Configuration

place application.yml to `<doc-l10n-kit working directory>/config/application.yml`

#### application.yml

```
translator:
  deepL:
    apiKey: <put your api key here>
  language:
    source: en   # default source language
    target: ja   # default target language
```

## Execution

### extract sentences from an original asciidoc file to a .po file
```
java -jar doc-l10n-kit-runner.jar asciidoc extract --asciidoc=<asciidoc file path> --po=<po file path>
```

### machine translate .po file
```
java -jar doc-l10n-kit-runner.jar po machine-translate --po=<po file path> --source=<source language> --target=<target language>
```

### translate asciidoc file by writing back translated sentences to the asciidoc file
```
java -jar doc-l10n-kit-runner.jar asciidoc translate --po=<po file path> --sourceAsciidoc=<asciidoc file path> --targetAsciidoc=<asciidoc file path>
```
