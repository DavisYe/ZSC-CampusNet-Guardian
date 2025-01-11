'use client';

import React, { useState } from 'react';
import {
  Box,
  Container,
  Paper,
  Typography,
  TextField,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  InputAdornment,
  Chip,
  List,
  ListItem,
  ListItemButton,
  ListItemText,
  Grid,
} from '@mui/material';
import {
  ExpandMore as ExpandMoreIcon,
  Search as SearchIcon,
  Folder as FolderIcon,
} from '@mui/icons-material';
import { useRequest } from '@/lib/hooks/useRequest';
import { knowledgeApi } from '@/lib/api/knowledge';
import { KnowledgeArticle, KnowledgeCategory } from '@/types';

interface ArticleListResponse {
  total: number;
  items: KnowledgeArticle[];
}

export default function FAQPage(): React.ReactElement {
  const [selectedCategory, setSelectedCategory] = useState<number | null>(null);
  const [keyword, setKeyword] = useState('');

  const { data: categories } = useRequest<KnowledgeCategory[]>(
    () => knowledgeApi.getCategories(),
    {
      immediate: true,
    }
  );

  const { data: articles } = useRequest<ArticleListResponse>(
    () =>
      knowledgeApi.getArticles({
        categoryId: selectedCategory || undefined,
        keyword: keyword || undefined,
        tags: ['FAQ'],
      }),
    {
      immediate: true,
    }
  );

  const handleCategoryClick = (categoryId: number | null): void => {
    setSelectedCategory(categoryId);
  };

  const handleSearch = (event: React.ChangeEvent<HTMLInputElement>): void => {
    setKeyword(event.target.value);
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Grid container spacing={3}>
        {/* 左侧分类列表 */}
        <Grid item xs={12} md={3}>
          <Paper sx={{ p: 2 }}>
            <Typography variant="h6" gutterBottom>
              问题分类
            </Typography>
            <List>
              <ListItem disablePadding>
                <ListItemButton
                  selected={selectedCategory === null}
                  onClick={() => handleCategoryClick(null)}
                >
                  <ListItemText primary="全部问题" />
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

        {/* 右侧FAQ列表 */}
        <Grid item xs={12} md={9}>
          <Paper sx={{ p: 2 }}>
            {/* 搜索框 */}
            <Box sx={{ mb: 3 }}>
              <TextField
                fullWidth
                placeholder="搜索常见问题..."
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

            {/* FAQ列表 */}
            <Box>
              {articles?.items.map((article) => (
                <Accordion key={article.id}>
                  <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                    <Box>
                      <Typography variant="subtitle1">{article.title}</Typography>
                      <Box sx={{ mt: 0.5 }}>
                        {article.tags?.map((tag) => (
                          <Chip
                            key={tag}
                            label={tag}
                            size="small"
                            color="primary"
                            variant="outlined"
                            sx={{ mr: 0.5 }}
                          />
                        ))}
                      </Box>
                    </Box>
                  </AccordionSummary>
                  <AccordionDetails>
                    <Typography
                      variant="body1"
                      color="text.secondary"
                      sx={{ mb: 2 }}
                    >
                      {article.content}
                    </Typography>
                    {article.content.includes('解决方案：') && (
                      <>
                        <Typography variant="subtitle2" gutterBottom>
                          解决方案：
                        </Typography>
                        <Typography
                          variant="body2"
                          sx={{
                            backgroundColor: 'action.hover',
                            p: 2,
                            borderRadius: 1,
                          }}
                        >
                          {article.content.split('解决方案：')[1].trim()}
                        </Typography>
                      </>
                    )}
                  </AccordionDetails>
                </Accordion>
              ))}
            </Box>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
}