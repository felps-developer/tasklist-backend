-- Migration: Add active column to tasks and task_lists
-- Description: Adiciona coluna active para soft delete nas tabelas de tarefas e listas

-- Adiciona coluna active na tabela tasks
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT true;

-- Adiciona coluna active na tabela task_lists
ALTER TABLE task_lists ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT true;

-- Índices para performance nas consultas que filtram por active
CREATE INDEX IF NOT EXISTS idx_tasks_active ON tasks(active);
CREATE INDEX IF NOT EXISTS idx_task_lists_active ON task_lists(active);

-- Comentários nas colunas
COMMENT ON COLUMN tasks.active IS 'Indica se a tarefa está ativa (soft delete)';
COMMENT ON COLUMN task_lists.active IS 'Indica se a lista está ativa (soft delete)';

