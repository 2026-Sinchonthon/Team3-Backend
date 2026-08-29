import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'Google 로그인 로컬 테스트',
  description: 'Team 3 백엔드 Google OAuth 로컬 테스트 페이지',
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko">
      <body>{children}</body>
    </html>
  );
}
