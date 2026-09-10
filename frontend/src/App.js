import React, { useState, useEffect } from 'react';
import { CssBaseline, Container, Box, Drawer } from '@mui/material';
import Navbar from './components/Navbar';
import ListaProdutos from './components/ListaProdutos';
import Carrinho from './components/Carrinho';
import Pagamento from './components/Pagamento';

function App() {
    const [carrinho, setCarrinho] = useState(null);
    const [pedidoFinalizado, setPedidoFinalizado] = useState(null);
    const [isCartOpen, setIsCartOpen] = useState(false);
    const [triggerAddProduct, setTriggerAddProduct] = useState(0);

    useEffect(() => {
        fetch('http://localhost:8080/api/carrinhos', { method: 'POST' })
            .then(response => response.json())
            .then(data => setCarrinho(data))
            .catch(error => console.error("Erro ao criar carrinho:", error));
    }, []);

    const adicionarAoCarrinho = (produtoId, quantidade = 1) => {
        if (!carrinho) return;
        fetch(`http://localhost:8080/api/carrinhos/${carrinho.id}/itens?produtoId=${produtoId}&quantidade=${quantidade}`, {
            method: 'POST'
        })
            .then(response => {
                if (!response.ok) throw new Error("Erro ao adicionar produto.");
                return response.json();
            })
            .then(carrinhoAtualizado => {
                setCarrinho(carrinhoAtualizado);
                setIsCartOpen(true);
            })
            .catch(error => alert(error.message));
    };

    const atualizarQuantidade = (produtoId, alteracao) => {
        if (alteracao > 0) {
            adicionarAoCarrinho(produtoId, alteracao);
        } else {
            fetch(`http://localhost:8080/api/carrinhos/${carrinho.id}/itens/${produtoId}/diminuir`, {
                method: 'POST'
            })
                .then(res => {
                    if (!res.ok) throw new Error("Erro ao diminuir produto.");
                    return res.json();
                })
                .then(carrinhoAtualizado => {
                    setCarrinho(carrinhoAtualizado);
                })
                .catch(error => alert(error.message));
        }
    };

    const removerItem = (produtoId) => {
        fetch(`http://localhost:8080/api/carrinhos/${carrinho.id}/itens/${produtoId}`, { method: 'DELETE' })
            .then(res => {
                if(!res.ok) throw new Error("O Backend não possui rota configurada para deletar itens do carrinho.");
                return res.json();
            })
            .then(data => setCarrinho(data))
            .catch(err => alert(err.message));
    };

    const realizarCheckout = () => {
        if (!carrinho) return;
        fetch(`http://localhost:8080/api/pedidos/checkout/${carrinho.id}`, { method: 'POST' })
            .then(response => response.json())
            .then(pedido => {
                setIsCartOpen(false);
                setPedidoFinalizado(pedido);
            })
            .catch(error => console.error("Erro no checkout:", error));
    };

    const resetarFluxo = () => {
        window.location.reload();
    };

    return (
        <>
            <CssBaseline />
            <Navbar
                itensNoCarrinho={carrinho?.itens?.length || 0}
                onCartClick={() => setIsCartOpen(true)}
                onAddProductClick={() => setTriggerAddProduct(prev => prev + 1)}
            />

            <Container maxWidth="xl" sx={{ mt: 4, mb: 4 }}>
                {pedidoFinalizado ? (
                    <Box display="flex" justifyContent="center">
                        <Pagamento pedido={pedidoFinalizado} onVoltar={resetarFluxo} />
                    </Box>
                ) : (
                    <ListaProdutos
                        adicionarAoCarrinho={(id) => adicionarAoCarrinho(id, 1)}
                        triggerAddProduct={triggerAddProduct}
                    />
                )}
            </Container>

            <Drawer anchor="right" open={isCartOpen} onClose={() => setIsCartOpen(false)}>
                <Box sx={{ width: { xs: '100vw', sm: 500 }, p: 1, height: '100%' }}>
                    <Carrinho
                        carrinho={carrinho}
                        realizarCheckout={realizarCheckout}
                        atualizarQuantidade={atualizarQuantidade}
                        removerItem={removerItem}
                    />
                </Box>
            </Drawer>
        </>
    );
}

export default App;