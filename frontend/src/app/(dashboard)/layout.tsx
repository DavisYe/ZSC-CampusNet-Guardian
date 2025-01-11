'use client';

import React, { useEffect } from 'react';
import {
  AppBar,
  Box,
  CssBaseline,
  Drawer,
  IconButton,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
  Divider,
  Button,
} from '@mui/material';
import {
  Menu as MenuIcon,
  Build as BuildIcon,
  LibraryBooks as LibraryBooksIcon,
  Person as PersonIcon,
  ExitToApp as ExitToAppIcon,
  Add as AddIcon,
  List as ListIcon,
} from '@mui/icons-material';
import { useRouter, usePathname } from 'next/navigation';
import { useAuth } from '@/lib/hooks/useAuth';

const drawerWidth = 240;

const menuItems = [
  {
    title: '故障报修',
    items: [
      { text: '提交报修', icon: <AddIcon />, path: '/repair/create' },
      { text: '报修记录', icon: <ListIcon />, path: '/repair/list' },
    ],
  },
  {
    title: '知识库',
    items: [
      { text: '故障知识库', icon: <LibraryBooksIcon />, path: '/knowledge' },
      { text: '常见问题', icon: <BuildIcon />, path: '/knowledge/faq' },
    ],
  },
];

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}): React.ReactElement | null {
  const [mobileOpen, setMobileOpen] = React.useState(false);
  const { isAuthenticated, isLoading, user, logout } = useAuth();
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      router.push('/login');
    }
  }, [isLoading, isAuthenticated, router]);

  if (isLoading || !isAuthenticated) {
    return null;
  }

  const handleDrawerToggle = (): void => {
    setMobileOpen(!mobileOpen);
  };

  const drawer = (
    <Box>
      <Toolbar>
        <Typography variant="h6" noWrap component="div">
          校园网络报修系统
        </Typography>
      </Toolbar>
      <Divider />
      {menuItems.map((group) => (
        <React.Fragment key={group.title}>
          <Typography
            variant="subtitle2"
            color="text.secondary"
            sx={{ px: 3, py: 1.5 }}
          >
            {group.title}
          </Typography>
          <List>
            {group.items.map((item) => (
              <ListItem key={item.text} disablePadding>
                <ListItemButton
                  selected={pathname === item.path}
                  onClick={() => router.push(item.path)}
                >
                  <ListItemIcon>{item.icon}</ListItemIcon>
                  <ListItemText primary={item.text} />
                </ListItemButton>
              </ListItem>
            ))}
          </List>
          <Divider />
        </React.Fragment>
      ))}
    </Box>
  );

  return (
    <Box sx={{ display: 'flex' }}>
      <CssBaseline />
      <AppBar
        position="fixed"
        sx={{
          width: { sm: `calc(100% - ${drawerWidth}px)` },
          ml: { sm: `${drawerWidth}px` },
        }}
      >
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mr: 2, display: { sm: 'none' } }}
          >
            <MenuIcon />
          </IconButton>
          <Box sx={{ flexGrow: 1 }} />
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <IconButton color="inherit" size="small">
              <PersonIcon />
            </IconButton>
            <Typography variant="subtitle2" noWrap component="div">
              {user?.username}
            </Typography>
            <Button
              color="inherit"
              startIcon={<ExitToAppIcon />}
              onClick={() => logout()}
            >
              退出
            </Button>
          </Box>
        </Toolbar>
      </AppBar>
      <Box
        component="nav"
        sx={{ width: { sm: drawerWidth }, flexShrink: { sm: 0 } }}
      >
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{
            keepMounted: true, // Better open performance on mobile.
          }}
          sx={{
            display: { xs: 'block', sm: 'none' },
            '& .MuiDrawer-paper': {
              boxSizing: 'border-box',
              width: drawerWidth,
            },
          }}
        >
          {drawer}
        </Drawer>
        <Drawer
          variant="permanent"
          sx={{
            display: { xs: 'none', sm: 'block' },
            '& .MuiDrawer-paper': {
              boxSizing: 'border-box',
              width: drawerWidth,
            },
          }}
          open
        >
          {drawer}
        </Drawer>
      </Box>
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          width: { sm: `calc(100% - ${drawerWidth}px)` },
        }}
      >
        <Toolbar />
        {children}
      </Box>
    </Box>
  );
}