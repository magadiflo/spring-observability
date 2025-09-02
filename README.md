# Observabilidad en Spring Boot: Métricas, Logs y Traces

## Grafana stack: `Prometheus`, `Grafana`, `Loki`, `Tempo`

- [Integrating Grafana Observability Stack into a Spring Boot Application: A Comprehensive Guide](https://medium.com/@narasimha4789/integrating-grafana-observability-stack-into-a-spring-boot-application-a-comprehensive-guide-eb9d21f29fe6)
- [Monitoring Spring Boot Applications with Prometheus and Grafana](https://blog.stackademic.com/monitoring-spring-boot-applications-with-prometheus-and-grafana-99805c27246a)
- [Monitor Spring reactive micro-services with Prometheus and Grafana: a how-to guide](https://blog.devops.dev/monitor-spring-reactive-micro-services-with-prometheus-and-grafana-a-how-to-guide-587b41c83156)

---

## 🔍 ¿Qué es la observabilidad?

Es la
`capacidad de entender qué está pasando dentro de tu sistema a partir de la información que expone (métricas, logs y traces)`,
sin necesidad de abrir el código o debuggear.

Un sistema observable te permite responder preguntas como:

- ¿Está funcionando bien la app?
- ¿Por qué los usuarios se quejan de lentitud?
- ¿Dónde está fallando un request?

> 🔑 La observabilidad se construye sobre `3 pilares`: `Métricas`, `Logs` y `Traces`.
>
> - `Métricas`, datos cuantitativos sobre el rendimiento del sistema y el uso de recursos.
> - `Logs`, registros detallados de eventos dentro de la aplicación.
> - `Traces`, información sobre el flujo de solicitudes entre servicios.

## 🧱 Los 3 pilares de la observabilidad

La observabilidad moderna se apoya en tres tipos de señales que, combinadas, permiten tener una visión completa del
sistema:

### 1. Métricas

Son valores numéricos (cuantitativos) que representan el estado o rendimiento del sistema y el uso de recursos.
Se miden a lo largo del tiempo y nos dan una visión global del estado del sistema.

Ejemplo en un Spring Boot de e-commerce:

- Latencia promedio de las peticiones `/checkout` → `250ms`.
- Cantidad de requests por segundo → `120 req/s`.
- Porcentaje de errores 5xx → `2%`.
- Uso de memoria JVM → `70%`.

💡 Si ves que la latencia promedio sube de `250ms` a `5s`, sabes que hay un problema de performance, pero no sabes
todavía dónde ni por qué.

> 📊 Herramientas:
> - `Prometheus`, recopila y almacena métricas en formato de series temporales.
> - `Micrometer`, instrumenta las aplicaciones de Spring Boot para exponer métricas.
> - `Grafana`, visualiza métricas con paneles y alertas.

Ejemplo de flujo de trabajo

- Su aplicación de Spring Boot expone métricas como la latencia de solicitudes HTTP mediante `Micrometer`.
- `Prometheus` extrae estas métricas del endpoint `/actuator/prometheus` a intervalos regulares.
- `Grafana` visualiza las métricas, lo que le ayuda a supervisar tendencias y configurar alertas para problemas de
  rendimiento.

### 2. Logs

Son registros detallados de eventos que describen algo que pasó en tu aplicación. Te dan contexto específico
(mucho más granular que las métricas).

Ejemplo en el mismo e-commerce:

````bash
2025-08-31 18:34:23 ERROR OrderService - Error al procesar la orden ORD-1234
java.lang.NullPointerException: ...
````

- Aquí sabes `qué orden falló`, `en qué servicio`, y `con qué excepción`.
- Es información puntual que complementa las métricas.

💡 Las `métricas` te dicen: “Hubo un `2%` de errores en checkout”. Los `logs` te dicen: “La orden `ORD-1234` falló
porque faltaba un campo en la request”.

> 📊 Herramientas:
> - `Loki`, una solución de logging (registro) ligera y escalable integrada con `Grafana`.
> - `Logback`, Spring Boot usa `Logback` como logging framework `por defecto`, el cual puede configurarse para enviar
    los logs a `Loki` (usando un `appender` específico o agentes como `Promtail`).

Ejemplo de flujo de trabajo

- Su aplicación registra logs como `"Fetching all items from the database"`.
- `Loki` recopila esos logs y permite realizar búsqueda con ellos.
- `Grafana` visualiza los logs, lo que permite correlacionarlos con métricas y traces.

### 3. Traces (Trazas)

Es el `camino completo que sigue un request a través de varios servicios`. Los `traces` proporcionan una visión
detallada de cómo fluye una solicitud a través del sistema, destacando los cuellos de botella y la latencia.
Cada operación dentro de la solicitud es un `span`; la colección de `spans` que representan todo el recorrido de la
solicitud conforma una `trace`.

Ejemplo en un sistema de microservicios:

- Un usuario hace un `POST /checkout`. La traza podría mostrar:
    - API Gateway → recibe la request `(10ms)`.
    - Order Service → crea la orden `(50ms`).
    - Payment Service → procesa el pago `(200ms)`.
    - Notification Service → envía email `(30ms)`.

💡 Gracias a la trace, ves que el request total tardó `290ms`, y que el mayor tiempo estuvo en el
`Payment Service (200ms)`. Esto sería casi imposible de saber solo con métricas o logs.

> 📊 Herramientas:
> - `Tempo (Grafana)`, recopila y almacena traces distribuidos.
> - `Micrometer Tracing`, se integra con librerías como `Brave` u `OpenTelemetry` para instrumentar las solicitudes y
    generar `traces`.

Ejemplo de flujo de trabajo

- Un request de usuario fluye a través de varios microservicios en su aplicación.
- `Tempo` recopila datos de seguimiento (trace), mostrando el tiempo empleado en cada servicio.
- `Grafana` visualiza estos traces, lo que ayuda a identificar servicios lentos.

## 🧠 ¿Por qué es importante entender esto?

Porque como desarrollador backend, cuando ocurre una incidencia, tú no solo necesitas saber `que algo está mal`,
sino `qué está mal`, `dónde` y `por qué`. La observabilidad bien implementada te da ese poder sin tener que hacer
suposiciones.

## Observabilidad vs Monitoreo

### Monitoreo (Monitoring)

El monitoreo es más `reactivo` y se enfoca en vigilar métricas predefinidas y conocidas. Es como tener alarmas que se
activan cuando algo específico va mal.

Características:

- Dashboards con `KPIs (Key Performance Indicators)` son indicadores clave de rendimiento que miden aspectos críticos
  del desempeño de tu aplicación:
    - `Técnicos`: CPU, memoria, tiempo de respuesta, throughput (rendimiento), tasa de errores.
    - `Funcionales`: Número de usuarios activos, transacciones por segundo, disponibilidad del servicio.
    - `Infraestructura`: Uso de disco, conexiones de base de datos, cola de requests.
- Se basa en métricas y alertas predefinidas.
- Responde a problemas conocidos y esperados.
- Enfoque en "¿está funcionando el sistema?".
- Alertas cuando los valores superan umbrales establecidos.

En `Spring Boot`: Actuator endpoints, métricas de Micrometer, health checks básicos.

### Observabilidad (Observability)

La observabilidad es más `proactiva` y te permite entender el estado interno del sistema basándote en sus salidas
externas. Es como ser un detective que puede investigar cualquier comportamiento extraño.

Características:

- Permite explorar y descubrir problemas desconocidos.
- Enfoque en "¿por qué está pasando esto?".
- Correlación entre métricas, logs y traces.
- Capacidad de hacer preguntas que no habías pensado antes.
- Contexto rico para debugging y troubleshooting.

En `Spring Boot`: Integración completa de los "tres pilares":

- `Métricas`: Micrometer + Prometheus + Grafana
- `Logs`: Structured logging + Loki + Grafana
- `Traces`: Micrometer Tracing + Tempo + Grafana (o Zipkin/Jaeger)

> `ELK (Elasticsearch, Logstash, Kibana)` es otra alternativa madura y ampliamente usada en entornos enterprise para
> logging y visualización.


La diferencia clave

- `Monitoreo`: "Mi API tiene `500ms` de latencia promedio" (me dice `QUÉ está pasando`)
- `Observabilidad`: "Mi API tiene `500ms` de latencia porque la consulta SQL en el servicio de usuarios está tardando
  `400ms` debido a un índice faltante en la tabla orders" (me dice `POR QUÉ está pasando`)

En `Spring Boot` moderno, la `observabilidad` se logra principalmente a través de `Micrometer Observation API`,
que unifica la recolección de estas señales de manera coherente. Es la evolución de `Micrometer` en `Spring Boot 3.x`
y constituye la base para integrar `métricas`, `logs` y `traces` en un mismo modelo.

### 🎯 ¿Cómo se complementan?

- `Monitoreo` te dice qué está pasando.
- `Observabilidad` te ayuda a entender por qué está pasando.

En sistemas modernos, el monitoreo es parte de la observabilidad, pero `la observabilidad va más allá`: te da
herramientas para investigar, correlacionar y resolver problemas complejos.

## Herramientas en el ecosistema

### Grafana Stack: Prometheus + Grafana + Loki + Tempo

- [Prometheus](https://prometheus.io/docs/introduction/overview/) es una herramienta de monitorización de código abierto
  diseñada para `recolectar métricas` en tiempo real mediante un modelo de extracción `(pull)`. Utiliza una base de
  datos de series temporales optimizada, un lenguaje de consulta potente `(PromQL)` y un sistema de alertas
  configurable. En otras palabras, `Prometheus` permite recopilar métricas de rendimiento y disponibilidad desde
  endpoints expuestos por servicios o aplicaciones, almacenarlas con etiquetas `(labels)` para facilitar su análisis, y
  generar alertas cuando se detectan condiciones anómalas.


- [Grafana](https://grafana.com/docs/grafana/latest/?pg=oss-graf&plcmt=hero-btn-2) es una `plataforma de visualización`
  de código abierto que permite explorar, analizar y correlacionar métricas, logs y trazas desde múltiples fuentes de
  datos. En otras palabras, `Grafana` se utiliza para construir `dashboards interactivos` que representan gráficamente
  el estado de los sistemas monitorizados, facilitando la detección de patrones, anomalías y tendencias. Además,
  permite configurar alertas visuales y notificaciones automáticas, integrándose de forma nativa con `Prometheus`,
  `Loki`, `Tempo` y otras herramientas del ecosistema de observabilidad. `Grafana` puede conectarse no solo con
  `Prometheus`, `Loki` y `Tempo`, sino también con bases `SQL`, `Elasticsearch`, `InfluxDB`, `CloudWatch`, etc.


- [Loki](https://grafana.com/docs/loki/latest/) es una herramienta de código abierto para la `gestión de logs`, diseñada
  por el equipo de Grafana. A diferencia de otros sistemas de logging que indexan el contenido completo de los logs,
  `Loki` se enfoca en indexar únicamente `metadatos (etiquetas)`, lo que permite una mayor eficiencia en almacenamiento
  y consulta. En otras palabras, `Loki` se utiliza para centralizar, almacenar y consultar registros de aplicaciones,
  manteniendo una arquitectura similar a la de Prometheus, lo que facilita su integración en entornos observables.
  `Loki` se integra fácilmente con `Grafana` para visualizar flujos de logs, configurar alertas y correlacionar eventos
  con métricas.


- [Tempo](https://grafana.com/docs/tempo/latest/) es una solución de código abierto para `trazabilidad distribuida`,
  desarrollada por Grafana Labs. Está diseñada para almacenar y consultar grandes volúmenes de trazas de aplicaciones
  sin necesidad de indexar cada evento individual, lo que permite una arquitectura más simple y escalable. En otras
  palabras, `Tempo` se utiliza para
  `capturar el flujo de ejecución de las solicitudes dentro de los sistemas distribuidos`, permitiendo identificar
  cuellos de botella, latencias y dependencias entre servicios. `Tempo` se integra fácilmente con herramientas de
  instrumentación como `OpenTelemetry`, `Jaeger` o `Zipkin`, y se apoya en sistemas de almacenamiento de objetos
  (como S3, GCS o MinIO) para guardar las trazas. Si ya usas `Prometheus` y `Loki`, `Tempo` completa el stack de
  observabilidad nativa de `Grafana`.

### ELK Stack: Elasticsearch + Logstash + Kibana

- [Elasticsearch](https://www.elastic.co/elasticsearch) es un motor de búsqueda y análisis distribuido basado en JSON,
  diseñado para almacenar, indexar y consultar grandes volúmenes de datos en tiempo real. En otras palabras,
  `Elasticsearch` se utiliza como base de datos para logs, permitiendo búsquedas rápidas y eficientes sobre registros
  estructurados y no estructurados.


- [Logstash](https://www.elastic.co/logstash) es una herramienta de ingesta de datos que permite recolectar, transformar
  y enviar logs desde múltiples fuentes hacia `Elasticsearch`. En otras palabras, `Logstash` actúa como un pipeline de
  procesamiento que recibe registros, los filtra y los enriquece antes de almacenarlos, facilitando la normalización y
  estructuración de los datos.


- [Kibana](https://www.elastic.co/kibana) es una plataforma de visualización que permite explorar los datos almacenados
  en `Elasticsearch` mediante dashboards interactivos, gráficos y alertas. En otras palabras, `Kibana` es la interfaz
  visual del stack `ELK`, utilizada para analizar logs, detectar patrones, y generar visualizaciones que apoyen la toma
  de decisiones operativas.

> `ELK` es `más pesado en consumo de recursos` que el `Grafana Stack`, pero sigue siendo popular en entornos enterprise
> por la potencia de `Elasticsearch`.

### Resumen

| Herramienta         | Enfoque principal  | Observaciones                                    |
|---------------------|--------------------|--------------------------------------------------|
| Prometheus          | `Métricas`         | Recolección con modelo *pull*                    |
| Grafana             | `Visualización`    | Conecta múltiples fuentes, dashboards y alertas  |
| Loki                | `Logs`             | Ligero, basado en labels, integra con Prometheus |
| Tempo/Zipkin/Jaeger | `Traces`           | Trazabilidad distribuida                         |
| ELK                 | `Logs` (principal) | Muy potente, pero más pesado en recursos         |

