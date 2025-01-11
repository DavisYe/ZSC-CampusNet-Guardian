'use client';

import React, { useState } from 'react';
import {
  Box,
  Container,
  Grid,
  Paper,
  Typography,
  TextField,
  List,
  ListItem,
  ListItemButton,
  ListItemText,
  Card,
  CardContent,
  CardActionArea,
  Chip,
  InputAdornment,
  TablePagination,
  Skeleton,
} from '@mui/material';
import {
  Search as SearchIcon,
  Folder as FolderIcon,
} from '@mui/icons-material';
import { useRouter } from 'next/navigation';
import { useRequest } from '@/lib/hooks/useRequest';
import { knowledgeApi } from '@/lib/api/knowledge';
import { KnowledgeArticle, KnowledgeCategory } from '@/types';
import dayjs from 'dayjs';

interface ArticleListResponse {
  total: number;
  items: KnowledgeArticle[];
}

export default function KnowledgePage(): React.ReactElement {
  const router = useRouter();
  const [selectedCategory, setSelectedCategory] = useState<number | null>(null);
  const [keyword, setKeyword] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const { data: categories } = useRequest<KnowledgeCategory[]>(
    () => knowledgeApi.getCategories(),
    {
      immediate: true,
    }
  );

  const { data: articles, isLoading } = useRequest<ArticleListResponse>(
    () =>
      knowledgeApi.getArticles({
        page: page + 1,
        size: rowsPerPage,
        categoryId: selectedCategory || undefined,
        keyword: keyword || undefined,
      }),
    {
      immediate: true,
    }
  );

  const handleChangePage = (_: unknown, newPage: number): void => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (
    event: React.ChangeEvent<HTMLInputElement>
  ): void => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleCategoryClick = (categoryId: number | null): void => {
    setSelectedCategory(categoryId);
    setPage(0);
  };

  const handleSearch = (event: React.ChangeEvent<HTMLInputElement>): void => {
    setKeyword(event.target.value);
    setPage(0);
  };

  const handleArticleClick = (articleId: number): void => {
    router.push(`/knowledge/${articleId}`);
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Grid container spacing={3}>
        {/* 左侧分类列表 */}
        <Grid item xs={12} md={3}>
          <Paper sx={{ p: 2 }}>
            <Typography variant="h6" gutterBottom>
              知识分类
            </Typography>
            <List>
              <ListItem disablePadding>
                <ListItemButton
                  selected={selectedCategory === null}
                  onClick={() => handleCategoryClick(null)}
                >
                  <ListItemText primary="全部分类" />
                </ListItemButton>
              </ListItem>
              {categories?.map((category) => (
                <ListItem key={category.id} disablePadding>
                  <ListItemButton
                    selected={selectedCategory === category.id}
                    onClick={() => handleCategoryClick(category.id)}
                  >
                    <FolderIcon sx={{ mr: 1, color: 'action.active' }} />
                    <ListItemText primary={category.name} />
                  </ListItemButton>
                </ListItem>
              ))}
            </List>
          </Paper>
        </Grid>

        {/* 右侧文章列表 */}
        <Grid item xs={12} md={9}>
          <Paper sx={{ p: 2 }}>
            {/* 搜索框 */}
            <Box sx={{ mb: 3 }}>
              <TextField
                fullWidth
                placeholder="搜索文章..."
                value={keyword}
                onChange={handleSearch}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <SearchIcon />
                    </InputAdornment>
                  ),
                }}
              />
            </Box>

            {/* 文章列表 */}
            <Grid container spacing={2}>
              {isLoading
                ? Array.from(new Array(3)).map((_, index) => (
                    <Grid item xs={12} key={index}>
                      <Skeleton
                        variant="rectangular"
                        height={200}
                        sx={{ borderRadius: 1 }}
                      />
                    </Grid>
                  ))
                : articles?.items.map((article) => (
                    <Grid item xs={12} key={article.id}>
                      <Card>
                        <CardActionArea
                          onClick={() => handleArticleClick(article.id)}
                        >
                          <CardContent>
                            <Typography variant="h6" gutterBottom>
                              {article.title}
                            </Typography>
                            <Typography
                              variant="body2"
                              color="text.secondary"
                              sx={{
                                overflow: 'hidden',
                                textOverflow: 'ellipsis',
                                display: '-webkit-box',
                                WebkitLineClamp: 2,
                                WebkitBoxOrient: 'vertical',
                                mb: 2,
                              }}
                            >
                              {article.content}
                            </Typography>
                            <Box
                              sx={{
                                display: 'flex',
                                justifyContent: 'space-between',
                                alignItems: 'center',
                              }}
                            >
                              <Box sx={{ display: 'flex', gap: 1 }}>
                                {article.tags?.map((tag) => (
                                  <Chip
                                    key={tag}
                                    label={tag}
                                    size="small"
                                    color="primary"
                                    variant="outlined"
                                  />
                                ))}
                              </Box>
                              <Typography variant="caption" color="text.secondary">
                                {dayjs(article.createdAt).format(
                                  'YYYY-MM-DD HH:mm'
                                )}
                              </Typography>
                            </Box>
                          </CardContent>
                        </CardActionArea>
                      </Card>
                    </Grid>
                  ))}
            </Grid>

            {/* 分页 */}
            <TablePagination
              component="div"
              count={articles?.total || 0}
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
        </Grid>
      </Grid>
    </Container>
  );
}