'use client';

import { ReactNode } from 'react';
import { ThemeProvider, createTheme, StyledEngineProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { Inter } from 'next/font/google';
import 'dayjs/locale/zh-cn';

const inter = Inter({ subsets: ['latin'] });

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
    background: {
      default: '#f5f5f5',
    },
  },
  typography: {
    fontFamily: inter.style.fontFamily,
  },
  components: {
    MuiAppBar: {
      defaultProps: {
        elevation: 4,
      },
      styleOverrides: {
        root: {
          backgroundColor: '#1976d2',
        },
      },
    },
    MuiPaper: {
      defaultProps: {
        elevation: 1,
      },
      styleOverrides: {
        root: {
          backgroundImage: 'none',
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
        },
      },
    },
    MuiTextField: {
      defaultProps: {
        size: 'small',
      },
    },
    MuiSelect: {
      defaultProps: {
        size: 'small',
      },
    },
  },
});

export function Providers({ children }: { children: ReactNode }) {
  return (
    <StyledEngineProvider injectFirst>
      <ThemeProvider theme={theme}>
        <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="zh-cn">
          <CssBaseline />
          {children}
        </LocalizationProvider>
      </ThemeProvider>
    </StyledEngineProvider>
  );
}