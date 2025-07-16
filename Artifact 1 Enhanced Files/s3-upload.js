const { S3Client, PutObjectCommand } = require("@aws-sdk/client-s3");
const { v4: uuidv4 } = require("uuid");
const multer = require("multer");
const upload = multer({ storage: multer.memoryStorage() });

// Initialize S3 client
const s3Client = new S3Client({
  region: process.env.AWS_REGION,
  credentials: {
    accessKeyId: process.env.AWS_ACCESS_KEY_ID,
    secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY,
  },
});

const bucketName = process.env.S3_BUCKET_NAME;

// Controller function for handling image uploads to S3
const uploadImage = async (req, res) => {
  try {
    if (!req.file) {
      return res.status(400).json({ error: "No file provided" });
    }

    // Validate file type
    const validTypes = ["image/jpeg", "image/png", "image/jpg", "image/gif"];
    if (!validTypes.includes(req.file.mimetype)) {
      return res
        .status(400)
        .json({ error: "Invalid file type. Only images are allowed." });
    }

    // Validate file size (5MB limit)
    const maxSize = 5 * 1024 * 1024; // 5MB
    if (req.file.size > maxSize) {
      return res.status(413).json({ error: "File size exceeds limit of 5MB" });
    }

    // Generate unique file name to prevent overwrites
    const fileKey = `${uuidv4()}-${req.file.originalname}`;

    // Prepare parameters for S3 upload
    const params = {
      Bucket: bucketName,
      Key: fileKey,
      Body: req.file.buffer,
      ContentType: req.file.mimetype,
    };

    // Upload to S3
    const command = new PutObjectCommand(params);
    await s3Client.send(command);

    // Construct the public URL for the uploaded object
    const imageUrl = `https://${bucketName}.s3.amazonaws.com/${fileKey}`;

    return res.status(200).json({ imageUrl });
  } catch (error) {
    console.error("Error uploading to S3:", error);
    return res.status(500).json({ error: "Failed to upload image" });
  }
};

// Middleware for handling the file upload
const handleImageUpload = upload.single("image");

module.exports = {
  uploadImage,
  handleImageUpload,
};
