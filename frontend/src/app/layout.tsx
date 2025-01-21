import { metadata } from './metadata';
import { Providers } from './providers';
import './globals.css';
import Navbar from '../components/layout/Navbar';

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}): React.ReactElement {
  return (
    <html lang="zh-CN">
      <body>
        <Providers>
          <Navbar />
          {children}
        </Providers>
      </body>
    </html>
  );
}

export { metadata };
