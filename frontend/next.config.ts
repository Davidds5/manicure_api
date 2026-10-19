import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  async rewrites() {
    return [
      {
        source: '/backend-api/:path*',
        destination: `${process.env.BACKEND_API_URL || 'https://manicure-api-vi63.onrender.com'}/:path*`,
      },
    ];
  },
};

export default nextConfig;
