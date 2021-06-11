package br.com.alura.leilao.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;

class GeradorDePagamentoTest {
	private GeradorDePagamento gerador;
	

	@Mock
	private PagamentoDao pagamentoDao;
	
	@Mock
	private Clock clock;
	
	@Captor
	//do mockito.
	private ArgumentCaptor<Pagamento> captor;

	@BeforeEach
	public void beforeEach() {
		MockitoAnnotations.initMocks(this);
		this.gerador = new GeradorDePagamento(pagamentoDao, clock);
	}


	@Test
	void deveriaCriarPagamentoParaVencedorLeilao() {
		Leilao leilao = leilao();
		Lance vencedor = leilao.getLanceVencedor();
		//data fixa para teste dos dias uteis porem hoje é sexta feira
		LocalDate data = LocalDate.of(2021, 06, 11);
		Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();
		
		Mockito.when(clock.instant()).thenReturn(instant);
		Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
		
		gerador.gerarPagamento(vencedor);
		//método para capturar o objeto passado como parametro
		Mockito.verify(pagamentoDao).salvar(captor.capture());
		
		Pagamento pagamento = captor.getValue();
		//por isso plusdays + 3 se fosse testado em outro dia da semana seria +1 
		Assert.assertEquals(LocalDate.now().plusDays(3), pagamento.getVencimento());
		Assert.assertEquals(vencedor.getValor(), pagamento.getValor());
		Assert.assertFalse(pagamento.getPago());
		Assert.assertEquals(vencedor.getUsuario(), pagamento.getUsuario());
		Assert.assertEquals(leilao, pagamento.getLeilao());
		
		
	}
	
	private Leilao leilao() {
		
		Leilao leilao = new Leilao("Celular", new BigDecimal("500"), new Usuario("Nathalia"));
		Lance lance = new Lance(new Usuario("Nathy"), new BigDecimal("900"));

		leilao.propoe(lance);
		leilao.setLanceVencedor(lance);

		return leilao;

	}

}
