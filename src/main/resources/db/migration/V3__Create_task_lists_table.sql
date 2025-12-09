-- Migration: Create task_lists table and add task_list_id to tasks
-- Description: Cria a tabela de listas de tarefas e adiciona relacionamento nas tarefas

-- Criar tabela de listas de tarefas
CREATE TABLE IF NOT EXISTS task_lists (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_task_lists_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Adicionar coluna task_list_id na tabela tasks
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS task_list_id UUID;

-- Adicionar foreign key para task_list_id
ALTER TABLE tasks 
    ADD CONSTRAINT fk_tasks_task_list 
    FOREIGN KEY (task_list_id) 
    REFERENCES task_lists(id) 
    ON DELETE SET NULL;

-- Índices para performance
CREATE INDEX IF NOT EXISTS idx_task_lists_user_id ON task_lists(user_id);
CREATE INDEX IF NOT EXISTS idx_tasks_task_list_id ON tasks(task_list_id);

-- Comentários nas colunas
COMMENT ON TABLE task_lists IS 'Tabela de listas de tarefas dos usuários';
COMMENT ON COLUMN task_lists.id IS 'Identificador único da lista (UUID)';
COMMENT ON COLUMN task_lists.name IS 'Nome da lista de tarefas';
COMMENT ON COLUMN task_lists.user_id IS 'Referência ao usuário proprietário da lista';
COMMENT ON COLUMN task_lists.created_at IS 'Data de criação do registro';
COMMENT ON COLUMN task_lists.updated_at IS 'Data da última atualização do registro';
COMMENT ON COLUMN tasks.task_list_id IS 'Referência à lista de tarefas (opcional)';

