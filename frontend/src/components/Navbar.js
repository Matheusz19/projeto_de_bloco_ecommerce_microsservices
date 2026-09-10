import React from 'react';
import { AppBar, Toolbar, Typography, IconButton, Badge, Box, Tooltip } from '@mui/material';

import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import StorefrontIcon from '@mui/icons-material/Storefront';
import AddIcon from '@mui/icons-material/Add';

function Navbar({ itensNoCarrinho, onCartClick, onAddProductClick }) {
    return (
        <Box sx={{ flexGrow: 1 }}>
            <AppBar position="static" sx={{ backgroundColor: '#1976d2' }}>
                <Toolbar>
                    <StorefrontIcon sx={{ mr: 2 }} />
                    <Typography variant="h6" component="div" sx={{ flexGrow: 1, fontWeight: 'bold' }}>
                        Plataforma E-commerce
                    </Typography>

                    <Tooltip title="Novo Produto">
                        <IconButton color="inherit" onClick={onAddProductClick} sx={{ mr: 2 }}>
                            <AddIcon />
                        </IconButton>
                    </Tooltip>

                    <Tooltip title="Carrinho de Compras">
                        <IconButton color="inherit" aria-label="carrinho" onClick={onCartClick}>
                            <Badge badgeContent={itensNoCarrinho} color="error">
                                <ShoppingCartIcon />
                            </Badge>
                        </IconButton>
                    </Tooltip>
                </Toolbar>
            </AppBar>
        </Box>
    );
}

export default Navbar;