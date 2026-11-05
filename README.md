# BAGUARD AI

### Intelligent Protection for Everyday Digital Life

**Built by BAPPARIS DEV**\
An Android security assistant concept focused on helping people
investigate suspicious messages, emails, SMS content, and links before
they act.

------------------------------------------------------------------------

## Overview

BAGUARD AI is an Android application designed to make everyday digital
safety easier to understand. Users can submit suspicious text or a URL
for investigation and receive a readable preliminary report that
highlights potentially concerning patterns and explains practical next
steps.

The project is built around a simple principle:

> Understand before you act.

BAGUARD AI aims to turn confusing security signals into clear,
actionable guidance---without asking users to be cybersecurity experts.

## Core capabilities

The current Android application includes:

-   **Security investigation interface** for entering suspicious
    messages, SMS, email text, or links.
-   **Preliminary local analysis** using pattern-based checks.
-   **Readable investigation reports** with a risk summary and
    supporting observations where available.
-   **Investigation history storage** using Room.
-   **Login and sign-up user interface**.
-   **Dark, security-focused interface** built with Jetpack Compose and
    Material 3.

### Current implementation status

This repository contains the Android application source code. The
investigation flow currently includes local pattern checks and should be
treated as a prototype.

The following capabilities are **not represented as fully operational
production features** in this repository:

-   Live Meta AI-powered investigation from the Android app.
-   Live URL reputation or threat-intelligence lookups.
-   Production authentication backed by a deployed account service.
-   AI voice conversation and call screening.

These are potential integration or development areas. Do not rely on a
preliminary prototype report as a definitive malware, phishing, or fraud
determination.

## Technology stack

  Area                    Technology
  ----------------------- ------------------------
  Platform                Android
  Language                Kotlin
  UI                      Jetpack Compose
  Design system           Material 3
  Local persistence       Room
  Build system            Gradle
  Dependency management   Gradle Version Catalog

## Project structure

``` text
BAGUARD-AI/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/bapparis/baguardai/
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/
│   │   │   │   │   ├── model/
│   │   │   │   │   └── remote/
│   │   │   │   ├── ui/theme/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── SignupScreen.kt
│   │   │   └── res/
│   │   ├── androidTest/
│   │   └── test/
│   └── build.gradle.kts
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Getting started

### Requirements

-   Android Studio with Android SDK 36 support.
-   JDK 11 or a compatible JDK supported by the project's Gradle/Android
    Gradle Plugin setup.
-   Internet access for Gradle dependency downloads.

### Clone the repository

``` bash
git clone https://github.com/bapparis/BAGUARD-AI.git
cd BAGUARD-AI
```

### Build the debug APK

On Windows PowerShell:

``` powershell
.\gradlew.bat :app:assembleDebug
```

On macOS or Linux:

``` bash
./gradlew :app:assembleDebug
```

The generated APK is normally located at:

``` text
app/build/outputs/apk/debug/app-debug.apk
```

Open the project in Android Studio to inspect the source, run the app,
or create a signed release build.

## Responsible use

BAGUARD AI is a security-assistance prototype, not a substitute for
professional incident response or a dedicated antivirus product. Its
preliminary analysis may produce false positives or miss malicious
content.

-   Do not share passwords, one-time codes, private keys, or other
    secrets in investigation inputs.
-   Do not open suspicious links merely to test them.
-   Verify urgent financial or account requests through an independently
    confirmed official channel.
-   Treat results as guidance, not a guarantee that content is safe or
    malicious.

## Hackathon

**Event:** Meta Global AI Developer Hackathon 2026\
**Team:** BAPPARIS DEV\
**Track:** Indie Developer Track\
**Problem statement:** Build for Your World\
**Project:** BAGUARD AI

**Demo:** [Watch the BAGUARD AI demo](https://youtu.be/u6zYv2I6li4)

## Project links

-   **Source code:** https://github.com/bapparis/BAGUARD-AI
-   **Demo video:** https://youtu.be/u6zYv2I6li4
-   **Developer:** [BAPPARIS DEV](https://github.com/bapparis)

## Contributing

Issues, suggestions, and constructive feedback are welcome. If you
propose a change, explain the problem it solves and include steps to
reproduce or verify it where relevant.

## License

No open-source license has been declared yet. Unless a license is added
to this repository, the source code remains subject to the rights of its
copyright holder; reuse, redistribution, and modification are not
automatically granted.
