import React, { useState, useEffect } from 'react';
import {
    Grid, Card, CardContent, CardActions, Typography, Button, Box,
    Dialog, DialogTitle, DialogContent, DialogActions, TextField
} from '@mui/material';

import AddShoppingCartIcon from '@mui/icons-material/AddShoppingCart';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

function ListaProdutos({ adicionarAoCarrinho, triggerAddProduct }) {
    const [produtos, setProdutos] = useState([]);
    const [openDialog, setOpenDialog] = useState(false);
    const [produtoEdit, setProdutoEdit] = useState({ nome: '', descricao: '', preco: '', quantidadeEstoque: '' });
    const [isEditing, setIsEditing] = useState(false);

    const buscarProdutos = () => {
        fetch('http://localhost:8080/api/produtos')
            .then(res => res.json())
            .then(data => setProdutos(data))
            .catch(err => console.error("Erro ao buscar:", err));
    };

    useEffect(() => {
        buscarProdutos();
    }, []);

    useEffect(() => {
        if (triggerAddProduct > 0) {
            handleOpenDialog();
        }
    }, [triggerAddProduct]);

    const handleOpenDialog = (produto = null) => {
        if (produto) {
            setProdutoEdit(produto);
            setIsEditing(true);
        } else {
            setProdutoEdit({ nome: '', descricao: '', preco: '', quantidadeEstoque: '' });
            setIsEditing(false);
        }
        setOpenDialog(true);
    };

    const handleSalvarProduto = () => {
        const url = isEditing
            ? `http://localhost:8080/api/produtos/${produtoEdit.id}`
            : 'http://localhost:8080/api/produtos';
        const method = isEditing ? 'PUT' : 'POST';

        fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(produtoEdit)
        }).then(() => {
            setOpenDialog(false);
            buscarProdutos();
        });
    };

    const handleExcluirProduto = (id) => {
        if(window.confirm("Deseja realmente excluir este produto?")) {
            fetch(`http://localhost:8080/api/produtos/${id}`, { method: 'DELETE' })
                .then(() => buscarProdutos())
                .catch(err => alert("Erro ao excluir. Verifique se o backend suporta esta ação."));
        }
    };

    return (
        <Box sx={{ p: 2 }}>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                <Typography variant="h5" component="h2" sx={{ fontWeight: 'bold', color: '#333' }}>
                    Catálogo de Produtos
                </Typography>
            </Box>

            <Grid container spacing={3}>
                {produtos.map(produto => (
                    <Grid item key={produto.id} xs={12} sm={6} md={4} lg={3}>
                        <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column', boxShadow: 3 }}>
                            <CardContent sx={{ flexGrow: 1 }}>
                                <Typography gutterBottom variant="h6" sx={{ fontWeight: 'bold' }}>
                                    {produto.nome}
                                </Typography>
                                <Typography variant="body2" color="text.secondary" sx={{ mb: 2, minHeight: '40px' }}>
                                    {produto.descricao}
                                </Typography>
                                <Typography variant="h6" color="primary" sx={{ fontWeight: 'bold' }}>
                                    R$ {produto.preco.toFixed(2)}
                                </Typography>
                                <Typography variant="caption" color={produto.quantidadeEstoque > 0 ? "text.secondary" : "error"}>
                                    Estoque: {produto.quantidadeEstoque}
                                </Typography>
                            </CardContent>

                            <CardActions sx={{ p: 2, pt: 0, flexDirection: 'column', gap: 1 }}>
                                <Button
                                    variant={produto.quantidadeEstoque === 0 ? "outlined" : "contained"}
                                    color="primary" fullWidth startIcon={<AddShoppingCartIcon />}
                                    onClick={() => adicionarAoCarrinho(produto.id)}
                                    disabled={produto.quantidadeEstoque === 0}
                                    sx={{ mb: 1 }}
                                >
                                    {produto.quantidadeEstoque === 0 ? 'Sem Estoque' : 'Comprar'}
                                </Button>

                                <Box display="flex" justifyContent="space-between" width="100%">
                                    <Button size="small" color="info" startIcon={<EditIcon />} onClick={() => handleOpenDialog(produto)}>
                                        Editar
                                    </Button>
                                    <Button size="small" color="error" startIcon={<DeleteIcon />} onClick={() => handleExcluirProduto(produto.id)}>
                                        Excluir
                                    </Button>
                                </Box>
                            </CardActions>
                        </Card>
                    </Grid>
                ))}
            </Grid>

            <Dialog open={openDialog} onClose={() => setOpenDialog(false)}>
                <DialogTitle>{isEditing ? 'Editar Produto' : 'Novo Produto'}</DialogTitle>
                <DialogContent>
                    <TextField autoFocus margin="dense" label="Nome" fullWidth variant="outlined"
                               value={produtoEdit.nome} onChange={e => setProdutoEdit({...produtoEdit, nome: e.target.value})} />
                    <TextField margin="dense" label="Descrição" fullWidth variant="outlined"
                               value={produtoEdit.descricao} onChange={e => setProdutoEdit({...produtoEdit, descricao: e.target.value})} />
                    <TextField margin="dense" label="Preço (R$)" type="number" fullWidth variant="outlined"
                               value={produtoEdit.preco} onChange={e => setProdutoEdit({...produtoEdit, preco: e.target.value})} />
                    <TextField margin="dense" label="Quantidade em Estoque" type="number" fullWidth variant="outlined"
                               value={produtoEdit.quantidadeEstoque} onChange={e => setProdutoEdit({...produtoEdit, quantidadeEstoque: e.target.value})} />
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenDialog(false)} color="error">Cancelar</Button>
                    <Button onClick={handleSalvarProduto} variant="contained" color="primary">Salvar</Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
}

export default ListaProdutos;