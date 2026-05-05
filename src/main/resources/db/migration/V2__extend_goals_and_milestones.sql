-- Safe upgrade for existing PostgreSQL data: add new Goal/Milestone columns with defaults,
-- then enforce NOT NULL where the JPA entity requires it. (Hibernate ddl-auto alone fails on
-- populated tables when adding NOT NULL columns without a server default.)

-- ========== goals ==========

ALTER TABLE goals ADD COLUMN IF NOT EXISTS status varchar(32);
UPDATE goals SET status = 'NOT_STARTED' WHERE status IS NULL;
ALTER TABLE goals ALTER COLUMN status SET DEFAULT 'NOT_STARTED';
ALTER TABLE goals ALTER COLUMN status SET NOT NULL;

ALTER TABLE goals ADD COLUMN IF NOT EXISTS priority varchar(32);
UPDATE goals SET priority = 'MEDIUM' WHERE priority IS NULL;
ALTER TABLE goals ALTER COLUMN priority SET DEFAULT 'MEDIUM';

ALTER TABLE goals ADD COLUMN IF NOT EXISTS start_date bigint;
ALTER TABLE goals ADD COLUMN IF NOT EXISTS completed_date bigint;

ALTER TABLE goals ADD COLUMN IF NOT EXISTS tags text;
ALTER TABLE goals ADD COLUMN IF NOT EXISTS notes text;

ALTER TABLE goals ADD COLUMN IF NOT EXISTS is_favorite boolean;
UPDATE goals SET is_favorite = false WHERE is_favorite IS NULL;
ALTER TABLE goals ALTER COLUMN is_favorite SET DEFAULT false;
ALTER TABLE goals ALTER COLUMN is_favorite SET NOT NULL;

ALTER TABLE goals ADD COLUMN IF NOT EXISTS is_pinned boolean;
UPDATE goals SET is_pinned = false WHERE is_pinned IS NULL;
ALTER TABLE goals ALTER COLUMN is_pinned SET DEFAULT false;
ALTER TABLE goals ALTER COLUMN is_pinned SET NOT NULL;

ALTER TABLE goals ADD COLUMN IF NOT EXISTS reminder_enabled boolean;
UPDATE goals SET reminder_enabled = false WHERE reminder_enabled IS NULL;
ALTER TABLE goals ALTER COLUMN reminder_enabled SET DEFAULT false;
ALTER TABLE goals ALTER COLUMN reminder_enabled SET NOT NULL;

ALTER TABLE goals ADD COLUMN IF NOT EXISTS reminder_frequency varchar(255);
ALTER TABLE goals ADD COLUMN IF NOT EXISTS motivation text;
ALTER TABLE goals ADD COLUMN IF NOT EXISTS expected_outcome text;

-- Indexes referenced by @Table on Goal (idempotent)
CREATE INDEX IF NOT EXISTS idx_goals_user_active ON goals (user_id, active);
CREATE INDEX IF NOT EXISTS idx_goals_user_status ON goals (user_id, status);
CREATE INDEX IF NOT EXISTS idx_goals_user_category ON goals (user_id, category);
CREATE INDEX IF NOT EXISTS idx_goals_user_favorite ON goals (user_id, is_favorite);
CREATE INDEX IF NOT EXISTS idx_goals_user_priority ON goals (user_id, priority);

-- ========== milestones ==========

ALTER TABLE milestones ADD COLUMN IF NOT EXISTS order_index integer;
UPDATE milestones SET order_index = 0 WHERE order_index IS NULL;
ALTER TABLE milestones ALTER COLUMN order_index SET DEFAULT 0;
ALTER TABLE milestones ALTER COLUMN order_index SET NOT NULL;

ALTER TABLE milestones ADD COLUMN IF NOT EXISTS priority varchar(32);
UPDATE milestones SET priority = 'MEDIUM' WHERE priority IS NULL;
ALTER TABLE milestones ALTER COLUMN priority SET DEFAULT 'MEDIUM';

ALTER TABLE milestones ADD COLUMN IF NOT EXISTS notes text;
ALTER TABLE milestones ADD COLUMN IF NOT EXISTS estimated_effort varchar(255);
ALTER TABLE milestones ADD COLUMN IF NOT EXISTS actual_effort varchar(255);
ALTER TABLE milestones ADD COLUMN IF NOT EXISTS reflection text;

CREATE INDEX IF NOT EXISTS idx_milestones_goal ON milestones (goal_id);
CREATE INDEX IF NOT EXISTS idx_milestones_completed ON milestones (is_completed, active);
