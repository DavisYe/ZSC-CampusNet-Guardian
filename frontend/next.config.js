/** @type {import('next').NextConfig} */
import withPWA from 'next-pwa'

const nextConfig = withPWA({
  dest: 'public',
  disable: process.env.NODE_ENV === 'development',
})({
  reactStrictMode: true,
  swcMinify: true,
  i18n: {
    locales: ['zh-CN'],
    defaultLocale: 'zh-CN',
  },
})

export default nextConfig