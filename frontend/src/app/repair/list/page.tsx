'use client';

import React, { useState } from 'react';
import {
  Box,
  Container,
  Paper,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  Chip,
  IconButton,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  SelectChangeEvent,
} from '@mui/material';
import { Visibility as VisibilityIcon } from '@mui/icons-material';
import { useRouter } from 'next/navigation';
import { useRequest } from '@/lib/hooks/useRequest';
import { repairApi } from '@/lib/api/repair';
import { RepairOrder, RepairOrderStatus, RepairOrderType } from '@/types';
import dayjs from 'dayjs';

const statusColors: Record<RepairOrderStatus, 'warning' | 'info' | 'error' | 'success'> = {
  [RepairOrderStatus.PENDING]: 'warning',
  [RepairOrderStatus.PROCESSING]: 'info',
  [RepairOrderStatus.ESCALATED]: 'error',
  [RepairOrderStatus.COMPLETED]: 'success',
};

const statusLabels: Record<RepairOrderStatus, string> = {
  [RepairOrderStatus.PENDING]: '待处理',
  [RepairOrderStatus.PROCESSING]: '处理中',
  [RepairOrderStatus.ESCALATED]: '已上报',
  [RepairOrderStatus.COMPLETED]: '已完成',
};

const typeLabels: Record<RepairOrderType, string> = {
  [RepairOrderType.NETWORK]: '网络故障',
  [RepairOrderType.HARDWARE]: '硬件故障',
  [RepairOrderType.SOFTWARE]: '软件故障',
  [RepairOrderType.OTHER]: '其他故障',
};

interface RepairOrderListResponse {
  total: number;
  items: RepairOrder[];
}

export default function RepairListPage(): React.ReactElement {
  const router = useRouter();
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [statusFilter, setStatusFilter] = useState<RepairOrderStatus | ''>('');

  const { data, isLoading, error } = useRequest<RepairOrderListResponse>(
    () =>
      repairApi.getOrders({
        page: page + 1,
        size: rowsPerPage,
        status: statusFilter || undefined,
      }),
    {
      immediate: true,
    }
  );

  const handleChangePage = (_: unknown, newPage: number): void => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>): void => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleStatusFilterChange = (event: SelectChangeEvent<RepairOrderStatus | ''>): void => {
    setStatusFilter(event.target.value as RepairOrderStatus | '');
    setPage(0);
  };

  const handleViewDetail = (id: number): void => {
    router.push(`/repair/${id}`);
  };

  if (error) {
    return (
      <Container maxWidth="lg">
        <Box sx={{ py: 4 }}>
          <Typography color="error">加载失败: {error.message}</Typography>
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg">
      <Box sx={{ py: 4 }}>
        <Paper sx={{ p: 3 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
            <Typography variant="h5" component="h1">
              我的报修记录
            </Typography>
            <FormControl sx={{ minWidth: 120 }}>
              <InputLabel>状态筛选</InputLabel>
              <Select
                value={statusFilter}
                label="状态筛选"
                onChange={handleStatusFilterChange}
              >
                <MenuItem value="">全部</MenuItem>
                {Object.values(RepairOrderStatus).map((status) => (
                  <MenuItem key={status} value={status}>
                    {statusLabels[status]}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Box>

          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>工单号</TableCell>
                  <TableCell>故障类型</TableCell>
                  <TableCell>故障地点</TableCell>
                  <TableCell>提交时间</TableCell>
                  <TableCell>状态</TableCell>
                  <TableCell>操作</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {isLoading ? (
                  <TableRow>
                    <TableCell colSpan={6} align="center">
                      加载中...
                    </TableCell>
                  </TableRow>
                ) : data?.items.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={6} align="center">
                      暂无数据
                    </TableCell>
                  </TableRow>
                ) : (
                  data?.items.map((order: RepairOrder) => (
                    <TableRow key={order.id}>
                      <TableCell>{order.id}</TableCell>
                      <TableCell>{typeLabels[order.type]}</TableCell>
                      <TableCell>{order.location}</TableCell>
                      <TableCell>
                        {dayjs(order.createdAt).format('YYYY-MM-DD HH:mm')}
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={statusLabels[order.status]}
                          color={statusColors[order.status]}
                          size="small"
                        />
                      </TableCell>
                      <TableCell>
                        <IconButton
                          size="small"
                          onClick={() => handleViewDetail(order.id)}
                          title="查看详情"
                        >
                          <VisibilityIcon />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </TableContainer>

          <TablePagination
            component="div"
            count={data?.total || 0}
            page={page}
            onPageChange={handleChangePage}
            rowsPerPage={rowsPerPage}
            onRowsPerPageChange={handleChangeRowsPerPage}
            labelRowsPerPage="每页行数"
            labelDisplayedRows={({ from, to, count }) =>
              `${from}-${to} 共 ${count !== -1 ? count : `超过 ${to}`} 条`
            }
          />
        </Paper>
      </Box>
    </Container>
  );
}