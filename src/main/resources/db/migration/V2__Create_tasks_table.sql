-- Migration: Create tasks table
-- Description: Cria a tabela de tarefas com relacionamento com usuários

CREATE TABLE IF NOT EXISTS tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índices para performance
CREATE INDEX IF NOT EXISTS idx_tasks_user_id ON tasks(user_id);
CREATE INDEX IF NOT EXISTS idx_tasks_completed ON tasks(completed);
CREATE INDEX IF NOT EXISTS idx_tasks_created_at ON tasks(created_at);

-- Comentários nas colunas
COMMENT ON TABLE tasks IS 'Tabela de tarefas dos usuários';
COMMENT ON COLUMN tasks.id IS 'Identificador único da tarefa (UUID)';
COMMENT ON COLUMN tasks.title IS 'Título da tarefa';
COMMENT ON COLUMN tasks.description IS 'Descrição detalhada da tarefa';
COMMENT ON COLUMN tasks.completed IS 'Indica se a tarefa está concluída';
COMMENT ON COLUMN tasks.user_id IS 'Referência ao usuário proprietário da tarefa';
COMMENT ON COLUMN tasks.created_at IS 'Data de criação do registro';
COMMENT ON COLUMN tasks.updated_at IS 'Data da última atualização do registro';

