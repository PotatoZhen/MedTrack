# MedTrack

MedTrack is an Android medication management application designed to help patients manage their medications, track symptoms, and access medication-related information.

## Features

* Patient login and authentication
* Medication management and tracking
* Medication intake tracking
* Symptom tracking with severity levels
* Medication information and interaction explanations
* AI-powered medication guidance using Gemini
* Drug information retrieved from the OpenFDA API
* REST API integration

## Tech Stack

* **Language:** Kotlin
* **UI:** Jetpack Compose, Material Design
* **Architecture:** MVVM
* **Database:** Room
* **Networking:** Retrofit
* **APIs:** OpenFDA API, Gemini API
* **Local Storage:** SharedPreferences
* **Development:** Android Studio
* **Version Control:** Git / GitHub

## Architecture

MedTrack follows the **MVVM (Model-View-ViewModel)** architecture to separate the user interface, application logic, and data layer.

UI (Jetpack Compose)
        ↓
    ViewModel
        ↓
   Repository
      ↙   ↘
 Room DB   APIs


## AI Integration

MedTrack integrates the **Gemini API** to provide medication-related guidance and explanations.

The application sends a structured prompt containing relevant medication information and the user's question to the Gemini API. The generated response is then displayed within the application.

AI-generated information is intended to provide general guidance and does not replace professional medical advice.



## What I Learned

Through this project, I gained experience in:

* Developing Android applications with Kotlin and Jetpack Compose
* Applying MVVM architecture
* Designing and managing a local database with Room
* Integrating REST APIs using Retrofit
* Working with external drug information APIs
* Integrating generative AI into an application
* Designing user-friendly interfaces for a healthcare-related application

## Project Status

**Completed**

## Disclaimer

MedTrack is an educational project. The information provided by the application is for general informational purposes and should not be considered a substitute for professional medical advice.

To add API key:
Add the following to local.properties:
GENAI_API_KEY= YOUR_API_KEY
