/**
 * BELASUNHAS — Teste de Carga e Estresse Concorrente (Node.js Nativo)
 * Simula até 1.000 requisições simultâneas em ondas progressivas
 */

const BASE_URL = process.env.TARGET_URL || 'https://manicure-api-vi63.onrender.com';
const CREDENTIALS = {
  email: 'teste@salao.com',
  password: '123456',
};

// Configuração das Ondas de Carga
const STAGES = [
  { name: 'Onda 1: Aquecimento', vus: 20, iterations: 1 },
  { name: 'Onda 2: Carga Média', vus: 100, iterations: 1 },
  { name: 'Onda 3: Carga Alta', vus: 300, iterations: 1 },
  { name: 'Onda 4: Estresse Severo', vus: 600, iterations: 1 },
  { name: 'Onda 5: PICO MÁXIMO (1.000 VUs)', vus: 1000, iterations: 1 },
];

async function runStage(stage) {
  console.log(`\n===============================================================`);
  console.log(`🚀 Executando: ${stage.name} (${stage.vus} requisições simultâneas)`);
  console.log(`===============================================================`);

  const metrics = {
    total: stage.vus,
    success200: 0,
    created201: 0,
    badRequest400: 0,
    rateLimit429: 0,
    forbiddenOrNotFound: 0,
    serverError500: 0,
    timeoutsOrErrors: 0,
    crossTenantLeaks: 0,
    doubleBookings: 0,
    latencies: [],
  };

  const startTime = Date.now();

  const promises = Array.from({ length: stage.vus }, async (_, idx) => {
    const reqStart = Date.now();
    try {
      // 1. Healthcheck / Ping
      const healthRes = await fetch(`${BASE_URL}/actuator/health`, {
        signal: AbortSignal.timeout(10000),
      }).catch(() => null);

      // 2. Login
      const loginRes = await fetch(`${BASE_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ login: CREDENTIALS.email, password: CREDENTIALS.password }),
        signal: AbortSignal.timeout(12000),
      }).catch(() => null);

      let token = null;
      if (loginRes && loginRes.status === 200) {
        const json = await loginRes.json().catch(() => ({}));
        token = json.token || json.tokenJWT;
      }

      // 3. Race Condition: Agendamento Concorrente no mesmo slot
      const apptRes = await fetch(`${BASE_URL}/api/v1/appointments`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
        },
        body: JSON.stringify({
          clientId: 1,
          professionalId: 1,
          serviceId: 1,
          dateTime: '2026-10-15T14:00:00',
        }),
        signal: AbortSignal.timeout(15000),
      }).catch(() => null);

      // 4. Teste de IDOR / Vazamento Cross-Tenant
      let idorRes = null;
      if (token) {
        idorRes = await fetch(`${BASE_URL}/api/v1/appointments/999999`, {
          headers: { Authorization: `Bearer ${token}` },
          signal: AbortSignal.timeout(10000),
        }).catch(() => null);
      }

      const reqDuration = Date.now() - reqStart;
      metrics.latencies.push(reqDuration);

      // Avaliação de Status
      if (!healthRes && !loginRes && !apptRes) {
        metrics.timeoutsOrErrors++;
        return;
      }

      if (loginRes) {
        if (loginRes.status === 200) metrics.success200++;
        else if (loginRes.status === 429) metrics.rateLimit429++;
        else if (loginRes.status >= 500) metrics.serverError500++;
      }

      if (apptRes) {
        if (apptRes.status === 201) metrics.doubleBookings++;
        else if (apptRes.status === 400) metrics.badRequest400++;
        else if (apptRes.status === 429) metrics.rateLimit429++;
        else if (apptRes.status >= 500) metrics.serverError500++;
      }

      if (idorRes && idorRes.status === 200) {
        metrics.crossTenantLeaks++;
      }
    } catch (e) {
      metrics.timeoutsOrErrors++;
    }
  });

  await Promise.allSettled(promises);
  const totalStageDuration = ((Date.now() - startTime) / 1000).toFixed(2);

  // Cálculo de percentis
  metrics.latencies.sort((a, b) => a - b);
  const p50 = metrics.latencies[Math.floor(metrics.latencies.length * 0.5)] || 0;
  const p95 = metrics.latencies[Math.floor(metrics.latencies.length * 0.95)] || 0;
  const p99 = metrics.latencies[Math.floor(metrics.latencies.length * 0.99)] || 0;

  console.log(`⏱️ Tempo total da onda: ${totalStageDuration}s`);
  console.log(`📊 Latência: P50=${p50}ms | P95=${p95}ms | P99=${p99}ms`);
  console.log(`✅ Sucesso 200/201: ${metrics.success200 + metrics.doubleBookings}`);
  console.log(`🛡️ Rate Limit (429 Bloqueados com Segurança): ${metrics.rateLimit429}`);
  console.log(`🛑 Bad Request 400 (Conflitos de Horário barrados): ${metrics.badRequest400}`);
  console.log(`⚠️ Erros 5xx / Quedas: ${metrics.serverError500}`);
  console.log(`⌛ Timeouts / Falhas de Conexão: ${metrics.timeoutsOrErrors}`);
  console.log(`🔒 Vazamentos Cross-Tenant (IDOR): ${metrics.crossTenantLeaks} (DEVE SER 0)`);
  console.log(`✂️ Agendamentos Criados no mesmo horário: ${metrics.doubleBookings} (DEVE SER <= 1)`);

  return metrics;
}

async function main() {
  console.log(`\n===============================================================`);
  console.log(`🧪 INICIANDO TESTE DE ESTRESSE & CARGA — BELASUNHAS API`);
  console.log(`🎯 Alvo: ${BASE_URL}`);
  console.log(`===============================================================`);

  for (const stage of STAGES) {
    await runStage(stage);
    console.log(`😴 Pausa de 3 segundos para alívio do pool de conexões...`);
    await new Promise((r) => setTimeout(r, 3000));
  }

  console.log(`\n===============================================================`);
  console.log(`🏁 TESTE DE ESTRESSE CONCLUÍDO COM SUCESSO!`);
  console.log(`===============================================================\n`);
}

main();
