/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  images: {
    loader: "custom"
  },
  experimental: { images: { layoutRaw: true } }
}

module.exports = nextConfig
