package br.com.zod.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import br.com.zod.model.ConsumoDispositivo
import kotlin.math.cos
import kotlin.math.sin

class PieChartView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var consumoDispositivos: List<ConsumoDispositivo> = emptyList()
    private val slicePaint = Paint().apply { isAntiAlias = true }
    private val labelPaint = Paint().apply {
        color = Color.BLACK
        textSize = 30f
        isAntiAlias = true
    }
    private val legendPaint = Paint().apply { isAntiAlias = true }


    private val sliceColors = listOf(
        Color.parseColor("#66BB6A"), // Verde
        Color.parseColor("#A5D6A7"), // Verde claro
        Color.parseColor("#388E3C"), // Verde escuro
        Color.parseColor("#8BC34A"), // Verde oliva
        Color.parseColor("#FF9800"), // Laranja
        Color.parseColor("#FFB74D"), // Laranja claro
        Color.parseColor("#F57C00"), // Laranja queimado
        Color.parseColor("#FF7043"), // Coral
        Color.parseColor("#FFEB3B"), // Amarelo
        Color.parseColor("#FFF176"), // Amarelo claro
        Color.parseColor("#FBC02D"), // Amarelo mostarda
        Color.parseColor("#FFD600"), // Dourado
        Color.parseColor("#E0E0E0"), // Cinza claro
        Color.parseColor("#616161"), // Cinza escuro
        Color.parseColor("#FAFAFA")  // Branco gelo
    )

    fun setConsumoDispositivos(data: List<ConsumoDispositivo>) {
        this.consumoDispositivos = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (consumoDispositivos.isEmpty()) return

        val consumoTotal = consumoDispositivos.sumOf { it.consumo }
        var startAngle = 0f


        val centerX = width / 2f
        val centerY = height / 2.5f
        val radius = (width.coerceAtMost(height) / 4).toFloat()

        consumoDispositivos.forEachIndexed { index, dispositivo ->
            val consumoProporcao = dispositivo.consumo / consumoTotal
            val sweepAngle = (consumoProporcao * 360).toFloat()

            // Desenhar fatia do gráfico
            slicePaint.color = sliceColors[index % sliceColors.size]
            canvas.drawArc(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius,
                startAngle,
                sweepAngle,
                true,
                slicePaint
            )

            // Adicionar rótulo (nome e porcentagem)
            val angleInRadians = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
            val labelX = (centerX + (radius * 0.7) * cos(angleInRadians)).toFloat()
            val labelY = (centerY + (radius * 0.7) * sin(angleInRadians)).toFloat()
            canvas.drawText(
                "${dispositivo.nome} (${(consumoProporcao * 100).toInt()}%)",
                labelX,
                labelY,
                labelPaint
            )

            startAngle += sweepAngle
        }


        drawLegend(canvas, centerX, centerY + radius + 80f)
    }

    private fun drawLegend(canvas: Canvas, startX: Float, startY: Float) {
        val itemHeight = 50f
        val boxSize = 30f

        consumoDispositivos.forEachIndexed { index, dispositivo ->
            val yOffset = startY + index * itemHeight


            legendPaint.color = sliceColors[index % sliceColors.size]
            canvas.drawRect(
                startX - 200f, yOffset,
                startX - 200f + boxSize,
                yOffset + boxSize,
                legendPaint
            )


            canvas.drawText(
                dispositivo.nome,
                startX - 150f + boxSize,
                yOffset + boxSize / 1.5f,
                labelPaint
            )
        }
    }
}
