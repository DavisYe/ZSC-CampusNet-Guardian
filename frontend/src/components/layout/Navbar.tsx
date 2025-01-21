'use client';

import Link from 'next/link';
import { useState } from 'react';

export default function Navbar() {
  const [isOpen, setIsOpen] = useState(false);

  const toggleMenu = () => {
    setIsOpen(!isOpen);
  };

  return (
    <nav className="bg-white dark:bg-gray-800 shadow-lg">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <Link href="/" className="text-gray-800 dark:text-white">
                <span className="font-bold text-xl">校园网络报修系统</span>
              </Link>
            </div>
            <div className="hidden md:block">
              <div className="ml-10 flex items-baseline space-x-4">
                <Link href="/" className="text-gray-800 dark:text-white hover:bg-gray-700 px-3 py-2 rounded-md text-sm font-medium">
                  首页
                </Link>
                <Link href="/repair/list" className="text-gray-800 dark:text-white hover:bg-gray-700 px-3 py-2 rounded-md text-sm font-medium">
                  维修订单
                </Link>
                <Link href="/repair/create" className="text-gray-800 dark:text-white hover:bg-gray-700 px-3 py-2 rounded-md text-sm font-medium">
                  创建订单
                </Link>
              </div>
            </div>
          </div>
          <div className="-mr-2 flex md:hidden">
            <button
              onClick={toggleMenu}
              type="button"
              className="inline-flex items-center justify-center p-2 rounded-md text-gray-800 dark:text-white hover:text-white hover:bg-gray-700 focus:outline-none"
            >
              <svg
                className="h-6 w-6"
                stroke="currentColor"
                fill="none"
                viewBox="0 0 24 24"
              >
                {isOpen ? (
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="2"
                    d="M6 18L18 6M6 6l12 12"
                  />
                ) : (
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="2"
                    d="M4 6h16M4 12h16M4 18h16"
                  />
                )}
              </svg>
            </button>
          </div>
        </div>
      </div>

      {isOpen && (
        <div className="md:hidden">
          <div className="px-2 pt-2 pb-3 space-y-1 sm:px-3">
            <Link href="/" className="text-gray-800 dark:text-white block px-3 py-2 rounded-md text-base font-medium">
              首页
            </Link>
            <Link href="/repair/list" className="text-gray-800 dark:text-white block px-3 py-2 rounded-md text-base font-medium">
              维修订单
            </Link>
            <Link href="/repair/create" className="text-gray-800 dark:text-white block px-3 py-2 rounded-md text-base font-medium">
              创建订单
            </Link>
          </div>
        </div>
      )}
    </nav>
  );
}