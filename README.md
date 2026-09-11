# 🏕 GreenHill Camp Telegram Bot

Telegram-бот для детского языкового лагеря **«GreenHill»**.  
Бот автоматизирует сбор заявок от родителей на смены, информирует о программе лагеря и моментально уведомляет администратора о новых записях.

---

## 🚀 Основной функционал

* **✏️ Сбор заявок (FSM / пошаговый диалог):**
  * Пошаговый опрос родителя: дата рождения ребёнка, ФИО ребёнка, контактный телефон.
  * Валидация формата введённых данных на каждом шаге.
  * Сохранение заявки в базу данных PostgreSQL с отметкой времени.
  * Отправка подтверждения родителю с итоговой сводкой.
* **🔔 Уведомления администратора:**
  * При создании новой заявки бот мгновенно отправляет детальное сообщение в Telegram администратору.
* **📌 Информация о смене:**
  * Отправка фотоальбома (медиагруппы) и подробного описания программы лагеря (квесты, языковые мастер-классы, условия, даты, стоимость).
* **📱 Контакты:**
  * Прямые контакты руководства, ссылки на соцсети (VK, Telegram-канал) и сайт для заявок.

---

## 🛠 Стек технологий

* **Язык & Платформа:** Java 21, Spring Boot 3.5.x
* **Telegram API:** Telegram Bots Spring Boot Starter (Long Polling)
* **База данных:** PostgreSQL 15, Spring Data JPA / Hibernate
* **Контейнеризация:** Docker, Docker Compose (multi-stage сборка)

---

## 📋 Требования к системе

### Локальная разработка:
* [Git](https://git-scm.com/)
* [Docker Desktop](https://www.docker.com/products/docker-desktop/)

### Сервер (VPS):
* **ОС:** Ubuntu 22.04 / 24.04 LTS или Debian 11 / 12
* **CPU:** от 1 vCPU
* **RAM:** рекомендуется **от 2 ГБ RAM** (для сборки Java-образа без проблем с памятью)  
  *Если у вас VPS с 1 ГБ RAM, обязательно создайте Swap-файл (см. ниже раздел про сервер).*
* **Диск:** от 15–20 ГБ свободного места.

---

## ⚡️ Быстрый старт (Запуск в 3 шага)

### Шаг 1. Клонируйте репозиторий

```bash
git clone https://github.com/your-username/greenhill_camp_bot.git
cd greenhill_camp_bot
```

### Шаг 2. Настройте файл переменных окружения `.env`

Создайте файл `.env` на основе шаблона `.env.example`:

```bash
cp .env.example .env
```

Откройте `.env` в любом текстовом редакторе (`nano .env`) и укажите ваши данные:

```env
# Токен бота, полученный у @BotFather в Telegram
BOT_TOKEN=your_telegram_bot_token_here

# Имя бота в Telegram
BOT_USERNAME=greenhill_camp_bot

# Telegram ID администратора (куда бот пересылает новые заявки)
# Узнать свой ID можно через бота @userinfobot
ADMIN_TG_ID=your_telegram_id_here

# Настройки базы данных (для сервера обязательно придумайте надежный пароль!)
POSTGRES_DB=greenhill_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_secure_password_here
```

### Шаг 3. Запустите проект

Выполните команду для сборки и фонового запуска всех сервисов:

```bash
docker compose up -d --build
```

Docker автоматически:
1. Запустит PostgreSQL 15 с политикой автоперезапуска `restart: always` и инициализирует базу `greenhill_db`.
2. Соберёт образ приложения на Java 21 через multi-stage build.
3. Дождётся готовности базы данных (`healthcheck`) и запустит Telegram-бота.

Проверить статус работы контейнеров:
```bash
docker compose ps
```

---

## 🌐 Развёртывание на сервере (Production)

### 1. Установка Docker на чистый сервер (Ubuntu/Debian)
Если на сервере ещё нет Docker, установите его официальным скриптом:
```bash
curl -fsSL https://get.docker.com -o get-docker.sh && sudo sh get-docker.sh
sudo usermod -aG docker $USER
```

### 2. Настройка Swap (если на сервере 1 ГБ RAM)
Сборка Maven с Java 21 во время `docker compose build` требует 1–1.5 ГБ RAM. Чтобы процесс не завершился ошибкой `Killed` (Out of Memory):
```bash
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

### 3. Безопасность базы данных
В `docker-compose.yaml` порт базы данных привязан к локальному адресу `127.0.0.1:5433:5432`.  
Это гарантирует, что база **закрыта от сканеров из публичного интернета**, а подключаться к ней можно локально или безопасно через SSH-туннель.

---

## 📊 Подключение к базе данных через DBeaver / DataGrip

### Сценарий А. При локальном запуске на компьютере
* **Хост (Host):** `localhost`
* **Порт (Port):** `5433`
* **База данных (Database):** `greenhill_db`
* **Пользователь:** `postgres`
* **Пароль:** пароль из `.env`

---

### Сценарий Б. Подключение к серверу (через безопасный SSH-туннель)
Так как база закрыта от внешнего интернета, в **DBeaver** настраивается безопасное подключение через SSH:

1. В настройках соединения (вкладка **Main**):
   * **Host:** `localhost`
   * **Port:** `5433`
   * **Database:** `greenhill_db`
   * **Username:** `postgres`
   * **Password:** пароль из вашего `.env` на сервере
2. Перейдите во вкладку **SSH**:
   * Поставьте галочку **Use SSH Tunnel**
   * **Host / IP:** публичный IP-адрес вашего сервера
   * **Port:** `22`
   * **User Name:** `root` (или пользователь сервера)
   * **Authentication Method:** Password или Private Key (ваш SSH-ключ)
3. Нажмите **Test Tunnel** -> **Test Connection**. DBeaver безопасно подключится к базе данных на сервере!

Все входящие заявки хранятся в таблице **`applications`**.

---

## 🔧 Полезные команды для управления

* **Посмотреть логи бота в реальном времени:**
  ```bash
  docker compose logs -f telegram_bot
  ```

* **Посмотреть логи базы данных:**
  ```bash
  docker compose logs -f postgres
  ```

* **Остановить проект:**
  ```bash
  docker compose stop
  ```

* **Запустить остановленный проект:**
  ```bash
  docker compose start
  ```

* **Перезапустить проект:**
  ```bash
  docker compose restart
  ```

* **Обновить код после git pull:**
  ```bash
  git pull
  docker compose up -d --build
  ```

* **Полная остановка и удаление контейнеров (данные в базе сохраняются):**
  ```bash
  docker compose down
  ```

* **Полный сброс базы данных (удаление всех таблиц и заявок):**
  ```bash
  docker compose down -v
  ```
