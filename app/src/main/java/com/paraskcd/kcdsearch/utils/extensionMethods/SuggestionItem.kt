package com.paraskcd.kcdsearch.utils.extensionMethods

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.composables.icons.heroicons.Heroicons
import com.composables.icons.heroicons.outline.ArrowTopRightOnSquare
import com.composables.icons.heroicons.outline.MagnifyingGlass
import com.composables.icons.heroicons.outline.User
import com.paraskcd.kcdsearch.model.SuggestionItem
import com.paraskcd.kcdsearch.ui.shared.components.iconCircle.IconCircle
import com.paraskcd.kcdsearch.ui.shared.components.iconCircle.IconCircleParams
import com.paraskcd.kcdsearch.ui.shared.components.suggestionItems.SuggestionListItemParams

fun SuggestionItem.toListItemParams(
    containerColor: Color,
    onClick: () -> Unit,
    getAppIcon: (String) -> ImageBitmap?,
    textIcon: ImageVector = Heroicons.Outline.MagnifyingGlass,
    trailing: @Composable (() -> Unit)? = null
): SuggestionListItemParams {
    return when (this) {
        is SuggestionItem.Text -> SuggestionListItemParams(
            headline = value,
            containerColor = containerColor,
            onClick = onClick,
            leading = { IconCircle(IconCircleParams(textIcon)) }
        )
        is SuggestionItem.App -> {
            SuggestionListItemParams(
                headline = item.label,
                supporting = item.packageName,
                containerColor = containerColor,
                onClick = onClick,
                leading = {
                    val icon = getAppIcon(item.packageName)
                    if (icon != null) {
                        Image(
                            bitmap = icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            )
        }
        is SuggestionItem.Contact -> SuggestionListItemParams(
            headline = item.name,
            supporting = item.number,
            containerColor = containerColor,
            onClick = onClick,
            leading = { IconCircle(IconCircleParams(Heroicons.Outline.User)) },
            trailing = trailing
        )
        is SuggestionItem.SearchAction -> {
            SuggestionListItemParams(
                headline = item.title,
                supporting = item.subtitle,
                containerColor = containerColor,
                onClick = onClick,
                leading = {
                    val icon = getAppIcon(item.packageName)
                    if (icon != null) {
                        Image(
                            bitmap = icon,
                            contentDescription = item.subtitle,
                            modifier = Modifier.size(40.dp)
                        )
                    } else {
                        IconCircle(IconCircleParams(Heroicons.Outline.ArrowTopRightOnSquare))
                    }
                }
            )
        }
    }
}