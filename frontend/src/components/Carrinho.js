import React, { useState, useEffect } from 'react';
import {
    Box, Typography, Button, Table, TableBody, TableCell,
    TableContainer, TableHead, TableRow, Paper, Divider, IconButton
} from '@mui/material';

import ShoppingCartCheckoutIcon from '@mui/icons-material/ShoppingCartCheckout';
import AddIcon from '@mui/icons-material/Add';
import RemoveIcon from '@mui/icons-material/Remove';
import DeleteIcon from '@mui/icons-material/Delete';

function Carrinho({ carrinho, realizarCheckout, atualizarQuantidade, removerItem }) {
    const [catalogo, setCatalogo] = useState({});

    useEffect(() => {
        fetch('http://localhost:8080/api/produtos')
            .then(res => res.json())
            .then(data => {
                const mapaProdutos = {};
                data.forEach(p => mapaProdutos[p.id] = p);
                setCatalogo(mapaProdutos);
            })
            .catch(err => console.error("Erro ao buscar catálogo para o carrinho:", err));
    }, [carrinho]);

    if (!carrinho || !carrinho.itens || carrinho.itens.length === 0) {
        return (
            <Paper elevation={3} sx={{ p: 4, textAlign: 'center', height: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
                <Typography variant="h6" color="text.secondary">
                    Seu carrinho está vazio.
                </Typography>
            </Paper>
        );
    }

    const itensAgrupados = carrinho.itens.reduce((acc, item) => {
        const itemExistente = acc.find(i => i.produtoId === item.produtoId);
        if (itemExistente) {
            itemExistente.quantidade += item.quantidade;
        } else {
            acc.push({ ...item });
        }
        return acc;
    }, []);

    const total = itensAgrupados.reduce((acc, item) => acc + (item.precoUnitario * item.quantidade), 0);

    return (
        <Paper elevation={3} sx={{ p: 2, display: 'flex', flexDirection: 'column', height: '100%' }}>
            <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 2 }}>
                Resumo do Pedido
            </Typography>

            <TableContainer component={Box} sx={{ mb: 2, flexGrow: 1 }}>
                <Table size="small">
                    <TableHead>
                        <TableRow>
                            <TableCell><strong>Produto</strong></TableCell>
                            <TableCell align="center"><strong>Qtd</strong></TableCell>
                            <TableCell align="right"><strong>Total</strong></TableCell>
                            <TableCell align="center"><strong>Ações</strong></TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {itensAgrupados.map((item, index) => {
                            const produtoInfo = catalogo[item.produtoId] || {};
                            const estoqueMaximo = produtoInfo.quantidadeEstoque || 0;
                            const nomeProduto = produtoInfo.nome || `Carregando...`;

                            return (
                                <TableRow key={index} hover>
                                    <TableCell>{nomeProduto}</TableCell>
                                    <TableCell align="center">
                                        <Box display="flex" alignItems="center" justifyContent="center">
                                            <IconButton size="small" color="error" onClick={() => atualizarQuantidade(item.produtoId, -1)} disabled={item.quantidade <= 1}>
                                                <RemoveIcon fontSize="small" />
                                            </IconButton>

                                            <Typography sx={{ mx: 1 }}>{item.quantidade}</Typography>

                                            <IconButton
                                                size="small" color="primary"
                                                onClick={() => atualizarQuantidade(item.produtoId, 1)}
                                                disabled={item.quantidade >= estoqueMaximo}
                                            >
                                                <AddIcon fontSize="small" />
                                            </IconButton>
                                        </Box>
                                    </TableCell>
                                    <TableCell align="right">
                                        <strong>R$ {(item.precoUnitario * item.quantidade).toFixed(2)}</strong>
                                    </TableCell>
                                    <TableCell align="center">
                                        <IconButton size="small" color="error" onClick={() => removerItem(item.produtoId)}>
                                            <DeleteIcon fontSize="small" />
                                        </IconButton>
                                    </TableCell>
                                </TableRow>
                            );
                        })}
                    </TableBody>
                </Table>
            </TableContainer>

            <Divider sx={{ my: 2 }} />

            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography variant="h6">Total Geral:</Typography>
                <Typography variant="h5" color="primary" sx={{ fontWeight: 'bold' }}>
                    R$ {total.toFixed(2)}
                </Typography>
            </Box>

            <Button
                variant="contained" color="success" size="large" fullWidth
                startIcon={<ShoppingCartCheckoutIcon />}
                onClick={realizarCheckout}
                sx={{ py: 1.5, fontWeight: 'bold' }}
            >
                Finalizar Pedido
            </Button>
        </Paper>
    );
}

export default Carrinho;