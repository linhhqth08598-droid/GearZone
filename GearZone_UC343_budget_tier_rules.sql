IF OBJECT_ID(N'[dbo].[budget_tier_rules]', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[budget_tier_rules] (
        [tier_id] [int] IDENTITY(1,1) NOT NULL,
        [tier_name] [nvarchar](100) NOT NULL,
        [min_budget] [decimal](18, 2) NOT NULL,
        [max_budget] [decimal](18, 2) NULL,
        [cpu_percent] [int] NOT NULL,
        [gpu_percent] [int] NOT NULL,
        [ram_percent] [int] NOT NULL,
        [storage_percent] [int] NOT NULL,
        [other_percent] [int] NOT NULL,
        [description] [nvarchar](255) NULL,
        [status] [nvarchar](20) NOT NULL DEFAULT ('ACTIVE'),
        CONSTRAINT [PK_budget_tier_rules] PRIMARY KEY CLUSTERED ([tier_id] ASC),
        CONSTRAINT [CK_budget_tier_range] CHECK (([max_budget] IS NULL OR [max_budget] > [min_budget])),
        CONSTRAINT [CK_budget_tier_sum] CHECK (((((([cpu_percent] + [gpu_percent]) + [ram_percent]) + [storage_percent]) + [other_percent]) = (100)))
    );
END
GO

IF NOT EXISTS (SELECT 1 FROM [dbo].[budget_tier_rules])
BEGIN
    INSERT INTO [dbo].[budget_tier_rules]
        ([tier_name], [min_budget], [max_budget], [cpu_percent], [gpu_percent], [ram_percent], [storage_percent], [other_percent], [description], [status])
    VALUES
        (N'Phổ thông (Dưới 15 triệu)', 3000000, 15000000, 22, 28, 12, 10, 28, N'Ưu tiên cân bằng CPU, RAM và lưu trữ cơ bản', 'ACTIVE'),
        (N'Tầm trung (15 - 30 triệu)', 15000000, 30000000, 20, 32, 12, 10, 26, N'Tăng tỷ trọng GPU cho gaming/đồ họa 1080p - 2K', 'ACTIVE'),
        (N'Cao cấp (Từ 30 triệu)', 30000000, NULL, 18, 36, 12, 10, 24, N'Ưu tiên GPU hiệu năng cao và nguồn/tản nhiệt tương xứng', 'ACTIVE');
END
GO
