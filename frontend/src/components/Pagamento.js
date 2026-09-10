import React, { useEffect, useState } from 'react';
import { Paper, Typography, Box, Divider, Button, CircularProgress } from '@mui/material';

import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ErrorIcon from '@mui/icons-material/Error';

function Pagamento({ pedido, onVoltar }) {
    const [detalhesPagamento, setDetalhesPagamento] = useState(null);
    const [erro, setErro] = useState(false);

    useEffect(() => {
        fetch(`http://localhost:8081/api/pagamentos/pedido/${pedido.id}`)
            .then(response => {
                if (!response.ok) throw new Error("Pagamento não encontrado");
                return response.json();
            })
            .then(data => setDetalhesPagamento(data))
            .catch(error => {
                console.error("Erro ao buscar pagamento", error);
                setErro(true);
            });
    }, [pedido.id]);

    return (
        <Paper elevation={4} sx={{ p: 4, width: '100%', maxWidth: 500, borderRadius: 3, textAlign: 'center' }}>
            <CheckCircleIcon color="success" sx={{ fontSize: 60, mb: 1 }} />
            <Typography variant="h5" sx={{ fontWeight: 'bold', color: '#2e7d32', mb: 2 }}>
                Pedido Concluído!
            </Typography>

            <Box sx={{ textAlign: 'left', mb: 2 }}>
                <Typography variant="body1"><strong>ID do Pedido:</strong> #{pedido.id}</Typography>
                <Typography variant="body1"><strong>Status:</strong> {pedido.status}</Typography>
                <Typography variant="h6" sx={{ mt: 1 }}><strong>Total:</strong> R$ {pedido.total?.toFixed(2)}</Typography>
            </Box>

            <Divider sx={{ borderStyle: 'dashed', my: 3 }} />

            <Typography variant="h6" color="primary" sx={{ mb: 2 }}>
                Recibo de Pagamento (Microsserviço)
            </Typography>

            {erro ? (
                <Box display="flex" alignItems="center" justifyContent="center" color="error.main">
                    <ErrorIcon sx={{ mr: 1 }} />
                    <Typography>Falha ao conectar na Porta 8081.</Typography>
                </Box>
            ) : detalhesPagamento ? (
                <Box sx={{ bgcolor: '#f5f5f5', p: 2, borderRadius: 2, textAlign: 'left' }}>
                    <Typography variant="body2"><strong>Transação:</strong> #{detalhesPagamento.id}</Typography>
                    <Typography variant="body2"><strong>Status:</strong> {detalhesPagamento.status}</Typography>
                    <Typography variant="body2"><strong>Data:</strong> {new Date(detalhesPagamento.dataProcessamento).toLocaleString()}</Typography>
                </Box>
            ) : (
                <CircularProgress size={30} />
            )}

            <Button
                variant="contained" color="primary" size="large" fullWidth sx={{ mt: 4, py: 1.5 }}
                onClick={onVoltar}
            >
                Fazer Nova Compra
            </Button>
        </Paper>
    );
}

export default Pagamento;