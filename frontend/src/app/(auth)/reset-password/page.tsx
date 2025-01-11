import { Suspense } from 'react';
import { Box, Container, CircularProgress } from '@mui/material';
import ResetPasswordForm from './reset-password-form';

export default function ResetPasswordPage() {
  return (
    <Container maxWidth="sm">
      <Box
        sx={{
          minHeight: '100vh',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          py: 4,
        }}
      >
        <Suspense
          fallback={
            <Box sx={{ display: 'flex', justifyContent: 'center' }}>
              <CircularProgress />
            </Box>
          }
        >
          <ResetPasswordForm />
        </Suspense>
      </Box>
    </Container>
  );
}