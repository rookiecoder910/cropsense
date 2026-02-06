🌱 CropSense – Crop Disease Detection App

CropSense is an Android application that uses Deep Learning (CNN) to detect crop diseases from leaf images.
Users can capture or upload a photo of a crop leaf and instantly receive the crop type, disease name, and confidence score.

📸 App Screenshot
<p align="center"> <img src="screenshots/app_ui.jpg" alt="CropSense App Screenshot" width="350"/> </p>
✨ Features

📷 Capture image using Camera

🖼️ Upload image from Gallery

🤖 CNN-based crop & disease classification

🌐 Backend powered by FastAPI

☁️ Deployed on Render

📊 Confidence score for predictions

🎨 Clean & modern Jetpack Compose UI

🏗️ Tech Stack
📱 Android (Frontend)

Kotlin

Jetpack Compose

MVVM Architecture

Retrofit + OkHttp

Coil (image loading)

🧠 Machine Learning

Convolutional Neural Network (CNN)

Trained on crop leaf disease dataset

🌐 Backend

FastAPI

Python

Uvicorn

Deployed on Render

🔁 App Workflow

User selects image (Camera / Gallery)

Image is sent to FastAPI backend

CNN model processes the image

Backend returns:

Crop name

Disease name

Confidence score

Result is displayed in the app

📡 API Endpoint
POST /predict
Content-Type: multipart/form-data
Body: file (image)


Example response:

{
  "crop": "Apple",
  "disease": "Cedar Apple Rust",
  "confidence": 0.99
}

🧪 Sample Prediction
Field	Value
Crop	Apple
Disease	Cedar Apple Rust
Confidence	99%
⚠️ Notes

Backend may take 10–20 seconds to respond on first request due to Render cold start.

Low-confidence predictions are flagged to improve user trust.

App does not store images locally or on server.

🚀 Future Improvements

🔍 Crop → Disease two-step classification

📊 Top-3 predictions

🧠 Grad-CAM visualization

💾 Prediction history

🌍 Offline handling & retry mechanism

👨‍💻 Author

Manas Kumar
Android & ML Enthusiast
